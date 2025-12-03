import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { Endereco } from '../interfaces/endereco.interface';
import { SortColumn, SortDirection } from '../diretives/sortable.diretive';
import { debounceTime, delay, switchMap, tap } from 'rxjs/operators';

interface SearchResult {
	enderecos: Endereco[];
	total: number;
}

interface State {
	page: number;
	pageSize: number;
	searchTerm: string;
	sortColumn: SortColumn<Endereco>;
	sortDirection: SortDirection;
}

const compare = (v1: string | number, v2: string | number) => (v1 < v2 ? -1 : v1 > v2 ? 1 : 0);

function sort(enderecos: Endereco[], column: SortColumn<Endereco>, direction: string): Endereco[] {
	if (direction === '' || column === '') {
		return enderecos;
	} else {
		return [...enderecos].sort((a, b) => {
			const res = compare(a[column], b[column]);
			return direction === 'asc' ? res : -res;
		});
	}
}

function matches(endereco: Endereco, term: string): boolean {
  return (
    endereco.logradouro.toLowerCase().includes(term.toLowerCase()) ||
    endereco.bairro.toLowerCase().includes(term.toLowerCase()) ||
    endereco.cidade.toLowerCase().includes(term.toLowerCase()) ||
    endereco.uf.toLowerCase().includes(term.toLowerCase()) ||
    endereco.cep.toLowerCase().includes(term.toLowerCase()) ||
    endereco.numero.toString().includes(term) // <- numero é number
  );
}

@Injectable({
  providedIn: 'root'
})
export class ListarEnderecosService {

	private _loading$ = new BehaviorSubject<boolean>(true);
	private _search$ = new Subject<void>();
	private _enderecos$ = new BehaviorSubject<Endereco[]>([]);
	private _total$ = new BehaviorSubject<number>(0);
  private enderecosCliente: Endereco[] = [];

	private _state: State = {
		page: 1,
		pageSize: 4,
		searchTerm: '',
		sortColumn: '',
		sortDirection: '',
	};

	constructor() { 
		this._search$
			.pipe(
				tap(() => this._loading$.next(true)),
				debounceTime(200),
				switchMap(() => this._search()),
				delay(200),
				tap(() => this._loading$.next(false)),
			)
			.subscribe((result) => {
				this._enderecos$.next(result.enderecos);
				this._total$.next(result.total);
			});
    console.log('Endereços do cliente:', this.enderecosCliente);
		this._search$.next();
	}

  setEnderecosCliente(enderecos: Endereco[]) {
    this.enderecosCliente = enderecos;
    this._search$.next();
  }

	get enderecos$() {
		return this._enderecos$.asObservable();
	}
	get total$() {
		return this._total$.asObservable();
	}
	get loading$() {
		return this._loading$.asObservable();
	}
	get page() {
		return this._state.page;
	}
	get pageSize() {
		return this._state.pageSize;
	}
	get searchTerm() {
		return this._state.searchTerm;
	}

	set page(page: number) {
		this._set({ page });
	}
	set pageSize(pageSize: number) {
		this._set({ pageSize });
	}
	set searchTerm(searchTerm: string) {
		this._set({ searchTerm });
	}
	set sortColumn(sortColumn: SortColumn<Endereco>) {
		this._set({ sortColumn });
	}
	set sortDirection(sortDirection: SortDirection) {
		this._set({ sortDirection });
	}

	private _set(patch: Partial<State>) {
		Object.assign(this._state, patch);
		this._search$.next();
	}

	private _search(): Observable<SearchResult> {
		const { sortColumn, sortDirection, pageSize, page, searchTerm } = this._state;

		// 1. sort
		let enderecos = sort(this.enderecosCliente, sortColumn, sortDirection);

		// 2. filter
		enderecos = enderecos.filter((endereco) => matches(endereco, searchTerm));
		const total = enderecos.length;

		// 3. paginate
		enderecos = enderecos.slice((page - 1) * pageSize, (page - 1) * pageSize + pageSize);
		return of({ enderecos, total });
	}
}