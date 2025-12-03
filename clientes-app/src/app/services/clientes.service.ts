import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../interfaces/cliente.interface';

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
export class ClientesService {

  private apiUrl = 'http://localhost:8080/api/v1/clientes'; 
  constructor(private http: HttpClient) {}

  obterClientes(
    pagina: number = 0,
    qtdRegistros: number = 20,
    campoOrdenacao: string = 'nome',
    tipoOrdenacao: string = 'asc'
  ): Observable<Page<Cliente>> {
    return this.http.get<Page<Cliente>>(
      `${this.apiUrl}?pagina=${pagina}&qtdRegistros=${qtdRegistros}&campoOrdenacao=${campoOrdenacao}&tipoOrdenacao=${tipoOrdenacao}`
    );
  }

  obterCliente(idCliente: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.apiUrl}/${idCliente}`);
  }

  criarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.apiUrl, cliente);
  }

  atualizarCliente(cliente: Cliente): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.apiUrl}/${cliente.id}`, cliente);
  }

  excluirCliente(idCliente: string): Observable<Cliente> {
    return this.http.delete<Cliente>(`${this.apiUrl}/${idCliente}`);
  }
  
}
