import { Component, inject } from '@angular/core';
import { ClientesService } from '../../../services/clientes.service';
import { Router } from '@angular/router';
import { FormGroup, Validators, ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';
import { FormEnderecoComponent } from '../../enderecos/form-endereco/form-endereco.component';

@Component({
  selector: 'app-criar-cliente',
  imports: [ ClienteFormComponent, FormEnderecoComponent, ReactiveFormsModule],
  templateUrl: './criar-cliente.component.html',
  styleUrl: './criar-cliente.component.css'
})
export class CriarClienteComponent {
  private readonly clienteService: ClientesService;
  private router = inject(Router);
  formCliente: FormGroup;

  constructor(private fb: FormBuilder) {
    this.clienteService = inject(ClientesService);
    this.formCliente = this.fb.group({
      nome: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(100)] ],
      cpf: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: ['', Validators.required],
      endereco: this.fb.group({
        logradouro: [''],
        numero: [''],
        complemento: [''],
        bairro: [''],
        cidade: [''],
        uf: [''],
        cep: ['']
      })
    });
  }

  clienteSalvoComSucessso(data: any){
    console.log('Cliente salvo com sucesso', data);
    this.router.navigate(['/clientes/detalhes', data.id]);
  }

  erroAoSalvarCliente(error: any){
    console.error('Erro ao salvar cliente', error);
    if(error.error && typeof error.error === 'object') {
      const formattedErrors = Object.entries(error.error)
        .map(([ _, message]) => `${message}`)
        .join('\n');

      alert('Erro ao salvar cliente:\n' + formattedErrors);
    } else {
      alert('Erro ao salvar cliente: ' + error.error);
    }
  }

  onSubmit() {
    // console.log('Formulário enviado');
    // console.log(this.formCliente.value);
    console.log( "[disabled]=" +!this.formCliente.status );
    const clienteAtualizado  = this.formCliente.value;
    this.clienteService.criarCliente(clienteAtualizado).subscribe({
      next: (data) => this.clienteSalvoComSucessso(data),
      error: (error) => this.erroAoSalvarCliente(error)
    });
  }

  get enderecoForm(): FormGroup {
    return this.formCliente.get('endereco') as FormGroup;
  }
  

}
