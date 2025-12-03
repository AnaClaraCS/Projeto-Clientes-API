import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { Cliente } from '../interfaces/cliente.interface';
import { SortColumn, SortDirection } from '../diretives/sortable.diretive';
import { debounceTime, delay, switchMap, tap } from 'rxjs/operators';
import { Endereco } from '../interfaces/endereco.interface';

interface SearchResult {
	clientes: Cliente[];
	total: number;
}

interface State {
	page: number;
	pageSize: number;
	searchTerm: string;
	sortColumn: SortColumn<Cliente>;
	sortDirection: SortDirection;
}

const compare = (v1: string | number | Date | Endereco[], v2: string | number | Date | Endereco[]) => (v1 < v2 ? -1 : v1 > v2 ? 1 : 0);

function sort(clientes: Cliente[], column: SortColumn<Cliente>, direction: string): Cliente[] {
	if (direction === '' || column === '') {
		return clientes;
	} else {
		return [...clientes].sort((a, b) => {
			const res = compare(a[column], b[column]);
			return direction === 'asc' ? res : -res;
		});
	}
}

function matches(cliente: Cliente, term: string): boolean {
  return (
    cliente.nome.toLowerCase().includes(term.toLowerCase()) ||
    cliente.email.toLowerCase().includes(term.toLowerCase()) ||
    cliente.cpf.toLowerCase().includes(term.toLowerCase()) ||
    cliente.dataNascimento.toString().includes(term.toLowerCase()) 
  );
}

@Injectable({
  providedIn: 'root'
})
export class ListarClientesService {

	private _loading$ = new BehaviorSubject<boolean>(true);
	private _search$ = new Subject<void>();
	private _clientes$ = new BehaviorSubject<Cliente[]>([]);
	private _total$ = new BehaviorSubject<number>(0);
  private clientesCliente: Cliente[] = [];

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
				this._clientes$.next(result.clientes);
				this._total$.next(result.total);
			});
    console.log('Endereços do cliente:', this.clientesCliente);
		this._search$.next();
	}

  setClientesCliente(clientes: Cliente[]) {
    this.clientesCliente = clientes;
    this._search$.next();
  }

	get clientes$() {
		return this._clientes$.asObservable();
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
	set sortColumn(sortColumn: SortColumn<Cliente>) {
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
		let clientes = sort(this.clientesCliente, sortColumn, sortDirection);

		// 2. filter
		clientes = clientes.filter((cliente) => matches(cliente, searchTerm));
		const total = clientes.length;

		// 3. paginate
		clientes = clientes.slice((page - 1) * pageSize, (page - 1) * pageSize + pageSize);
		return of({ clientes, total });
	}
}