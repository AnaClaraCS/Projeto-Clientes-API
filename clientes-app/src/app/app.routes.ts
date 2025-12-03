import { Routes } from '@angular/router';
import { ClientesComponent } from './pages/clientes/clientes.component';
import { ClienteDetalhesComponent } from './pages/clientes/cliente-detalhes/cliente-detalhes.component';
import { CriarClienteComponent } from './pages/clientes/criar-cliente/criar-cliente.component';
import { EditarClienteComponent } from './pages/clientes/editar-cliente/editar-cliente.component';
import { EditarEnderecoComponent } from './pages/enderecos/editar-endereco/editar-endereco.component';
import { CriarEnderecoComponent } from './pages/enderecos/criar-endereco/criar-endereco.component';

export const routes: Routes = [
  { path: '', redirectTo: 'clientes', pathMatch: 'full' },

  { path: 'clientes', component: ClientesComponent, title: 'Lista de Clientes' },
  { path: 'clientes/detalhes/:id', component: ClienteDetalhesComponent, title: 'Detalhes do cliente'},
  { path: 'clientes/novo', component: CriarClienteComponent, title: 'Novo Cliente' },
  { path: 'clientes/editar/:id', component: EditarClienteComponent, title: 'Editar Cliente' },

  { path: 'clientes/enderecos/novo/:idCliente', component: CriarEnderecoComponent, title: 'Novo Endereço' },
  { path: 'clientes/enderecos/editar/:id', component: EditarEnderecoComponent, title: 'Editar Endereço' }
];
