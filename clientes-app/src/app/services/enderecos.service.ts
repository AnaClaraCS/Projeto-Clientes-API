import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Endereco } from '../interfaces/endereco.interface';

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // página atual
}

@Injectable({
  providedIn: 'root'
})
export class EnderecosService {

  private apiUrl = 'http://localhost:8080/api/v1/enderecos'; 
  constructor(private http: HttpClient) {}

  obterEnderecos(
    pagina: number = 0,
    qtdRegistros: number = 20,
    campoOrdenacao: string = 'nome',
    tipoOrdenacao: string = 'asc'
  ): Observable<Page<Endereco>> {
    return this.http.get<Page<Endereco>>(
      `${this.apiUrl}?pagina=${pagina}&qtdRegistros=${qtdRegistros}&campoOrdenacao=${campoOrdenacao}&tipoOrdenacao=${tipoOrdenacao}`
    );
  }

  obterEndereco(idEndereco: string): Observable<Endereco> {
    return this.http.get<Endereco>(`${this.apiUrl}/${idEndereco}`);
  }

  criarEndereco(endereco: Endereco, idCliente: string): Observable<Endereco> {
    console.log(`${this.apiUrl}/${idCliente}`);
    return this.http.post<Endereco>(`${this.apiUrl}/${idCliente}`, endereco);
  }

  atualizarEndereco(endereco: Endereco): Observable<Endereco> {
    return this.http.put<Endereco>(`${this.apiUrl}/${endereco.id}`, endereco);
  }

  excluirEndereco(idEndereco: string): Observable<Endereco> {
    return this.http.delete<Endereco>(`${this.apiUrl}/${idEndereco}`);
  }
  
}
