import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { Router, RouterLink } from '@angular/router';

import { ClientesService } from '../../services/clientes.service';
import { Cliente } from '../../interfaces/cliente.interface';
import { map } from 'rxjs/operators';

import { NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [CommonModule, NgbPaginationModule, FormsModule],
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.css']
})
export class ClientesComponent {
  private readonly clienteService = inject(ClientesService);

  // Atributos da paginação
  page = 1;        // página atual (começa na 1)
  pageSize = 10;    // quantos clientes por página
  totalItems = 0;  // total de clientes (vem do backend)
  totalPages=0;

  clientes$!: Observable<Cliente[]>;
  service: any;
  private router = inject(Router);

  ngOnInit() {
    this.obterClientes();
  }

  // Quando a pagina mudar, chama obterClientes novamente
  onPageChange(pagina: number) {
    this.page = pagina;
    this.obterClientes(pagina);
  }

  obterClientes(
    pagina: number = 1,
    qtdRegistros: number = 20,
    campoOrdenacao: string = 'nome',
    tipoOrdenacao: string = 'asc'
  ) {
    this.clientes$ = this.clienteService.obterClientes(
      pagina=pagina-1, 
      qtdRegistros=this.pageSize, 
      campoOrdenacao=campoOrdenacao,
      tipoOrdenacao=tipoOrdenacao
    ).pipe(
      map(page => {
        this.totalPages = page.totalPages; // pega total da API
        this.totalItems = page.totalElements;
        return page.content; // só os clientes
      })
    );
  }

  verCliente(id: string) {
    this.router.navigate(['/clientes/detalhes/', id ]);
  }

  onPageSizeChange(newSize: number) {
    this.pageSize = Number(newSize);
    this.page = 1;
    this.obterClientes(this.page); // recarrega os clientes
  }

}
