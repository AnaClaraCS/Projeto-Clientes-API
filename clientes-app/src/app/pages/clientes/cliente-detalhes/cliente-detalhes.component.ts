import { Component, inject, Input } from '@angular/core';
import { Cliente } from '../../../interfaces/cliente.interface';
import { ClientesService } from '../../../services/clientes.service';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ListarEnderecosComponent } from '../../enderecos/listar-enderecos/listar-enderecos.component';

@Component({
  selector: 'app-cliente-detalhes',
  imports: [ RouterLink, ListarEnderecosComponent],
  templateUrl: './cliente-detalhes.component.html',
  styleUrl: './cliente-detalhes.component.css'
})
export class ClienteDetalhesComponent {
  private readonly clienteService = inject(ClientesService);
  private readonly route = inject(ActivatedRoute);
  private router = inject(Router);;
  
  idCliente!: string;
  cliente!: Cliente;

  ngOnInit() {
    this.idCliente = this.route.snapshot.paramMap.get('id')!;
    this.clienteService.obterCliente(this.idCliente)
        .subscribe(c => this.cliente = c);
  }

  deletarCliente(id: string) {
    this.clienteService.excluirCliente(id).subscribe(() => {
      alert('Cliente deletado com sucesso!');
      this.router.navigate(['/clientes']);
    });
  }


}
