import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EnderecosService } from '../../../services/enderecos.service';
import { FormEnderecoComponent } from '../form-endereco/form-endereco.component';

@Component({
  selector: 'app-criar-endereco',
  standalone: true,
  imports: [FormEnderecoComponent, ReactiveFormsModule],
  templateUrl: './criar-endereco.component.html',
  styleUrl: './criar-endereco.component.css'
})
export class CriarEnderecoComponent {
  private readonly enderecoService = inject(EnderecosService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  idCliente!: string;
  formEndereco: FormGroup;

  constructor(private fb: FormBuilder) {
    this.formEndereco = this.fb.group({
      logradouro: [''],
      numero: [''],
      bairro: [''],
      cidade: [''],
      uf: [''],
      complemento: [''],
      cep: ['']
    });
  }

  ngOnInit() {
    this.idCliente = this.route.snapshot.paramMap.get('idCliente')!;
    console.log('ID do cliente:', this.idCliente);
  }

  enderecoSalvoComSucessso(data: any) {
    console.log('Endereço salvo com sucesso', data);
    this.router.navigate(['/clientes/detalhes', this.idCliente]);
  }

  erroAoSalvarEndereco(error: any) {
    console.error('Erro ao salvar endereço', error);
    if (error.error && typeof error.error === 'object') {
      const formattedErrors = Object.entries(error.error)
        .map(([_, message]) => `${message}`)
        .join('\n');
      alert('Erro ao salvar endereco:\n' + formattedErrors);
    } else {
      alert('Erro ao salvar endereco: ' + error.error);
    }
  }

  onSubmit() {
    console.log('Formulário enviado');
    console.log(this.formEndereco.value);
    console.log('ID do cliente:', this.idCliente);

    const enderecoAtualizado = this.formEndereco.value;

    this.enderecoService.criarEndereco(enderecoAtualizado, this.idCliente).subscribe({
      next: (data) => this.enderecoSalvoComSucessso(data),
      error: (error) => this.erroAoSalvarEndereco(error)
    });
  }
}
