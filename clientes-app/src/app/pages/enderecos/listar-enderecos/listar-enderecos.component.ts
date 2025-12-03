import { Component, inject, Input, QueryList, ViewChildren  } from '@angular/core';
import { Endereco } from '../../../interfaces/endereco.interface';
import { AsyncPipe } from '@angular/common';

import { Observable } from 'rxjs';

import { NgbdSortableHeader, SortEvent } from '../../../diretives/sortable.diretive';
import { FormsModule } from '@angular/forms';
import { NgbHighlight, NgbPaginationModule } from '@ng-bootstrap/ng-bootstrap';
import { EnderecosService } from '../../../services/enderecos.service';
import { ListarEnderecosService } from '../../../services/listar-enderecos.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-listar-enderecos',
  imports: [ FormsModule, AsyncPipe, NgbHighlight, NgbdSortableHeader, NgbPaginationModule],
  templateUrl: './listar-enderecos.component.html',
  styleUrl: './listar-enderecos.component.css',
  providers: [EnderecosService],

})
export class ListarEnderecosComponent {
  @Input()
  enderecos: Endereco[] = [];
  private router = inject(Router);
  serviceEndereco = inject(EnderecosService);

  enderecos$!: Observable<Endereco[]>;
  total$!: Observable<number>;

  @ViewChildren(NgbdSortableHeader) headers!: QueryList<NgbdSortableHeader<Endereco>>;

  constructor(public service: ListarEnderecosService) {
    this.service.setEnderecosCliente(this.enderecos);
    this.enderecos$ = service.enderecos$;
    this.total$ = service.total$;
  }

  ngOnInit(){
    console.log('Endereços recebidos como input:', this.enderecos);
    console.log('Endereços do serviço:', this.enderecos$);
    this.service.setEnderecosCliente(this.enderecos);
    this.enderecos$ = this.service.enderecos$;
    this.total$ = this.service.total$;
  }

  onSort({ column, direction }: SortEvent<Endereco>) {
    // resetting other headers
    this.headers.forEach((header) => {
      if (header.sortable !== column) {
        header.direction = '';
      }
    });
    this.service.sortColumn = column;
    this.service.sortDirection = direction;
  }

  editarEndereco(id: string) {
    this.router.navigate(['clientes/enderecos/editar/', id ]);
  }

  deletarEndereco(id: string) {
    this.serviceEndereco.excluirEndereco(id).subscribe({
      next: () => {
        // Atualiza a lista de endereços após a exclusão
        this.enderecos = this.enderecos.filter(endereco => endereco.id !== id);
        this.service.setEnderecosCliente(this.enderecos);
        this.enderecos$ = this.service.enderecos$;
        this.total$ = this.service.total$;
        alert('Endereço excluído com sucesso!');
      }
    });
  }
}
