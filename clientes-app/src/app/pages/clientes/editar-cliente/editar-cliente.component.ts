import { Component, inject, Input } from '@angular/core';
import { Cliente } from '../../../interfaces/cliente.interface';
import { ClientesService } from '../../../services/clientes.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';

@Component({
  selector: 'app-editar-cliente',
  imports: [ClienteFormComponent, ReactiveFormsModule],
  templateUrl: './editar-cliente.component.html',
  styleUrl: './editar-cliente.component.css'
})
export class EditarClienteComponent {

  private readonly clienteService: ClientesService;
  private router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  formCliente: FormGroup;
  
  idCliente!: string;
  cliente!: Cliente;

  constructor(private fb: FormBuilder) {
    this.clienteService = inject(ClientesService);
    this.formCliente = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      dataNascimento: ['', Validators.required]
    });
  }

  ngOnInit() {    
    this.idCliente = this.route.snapshot.paramMap.get('id')!;
    this.clienteService.obterCliente(this.idCliente)
        .subscribe(c => {
          this.cliente = c;
          this.formCliente.patchValue(this.cliente);
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
    const clienteAtualizado : Cliente = this.formCliente.value;
    clienteAtualizado.id = this.cliente.id;
    this.clienteService.atualizarCliente(clienteAtualizado).subscribe({
      next: (data) => this.clienteSalvoComSucessso(data),
      error: (error) => this.erroAoSalvarCliente(error)
    });     
  }

}
