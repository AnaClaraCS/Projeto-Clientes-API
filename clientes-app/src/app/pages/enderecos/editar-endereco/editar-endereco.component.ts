import { Component, inject } from '@angular/core';
import { EnderecosService } from '../../../services/enderecos.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Endereco } from '../../../interfaces/endereco.interface';
import { FormEnderecoComponent } from '../form-endereco/form-endereco.component';

@Component({
  selector: 'app-editar-endereco',
  imports: [ FormEnderecoComponent, ReactiveFormsModule],
  templateUrl: './editar-endereco.component.html',
  styleUrl: './editar-endereco.component.css'
})
export class EditarEnderecoComponent {
private readonly enderecoService : EnderecosService;
  private router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  idEndereco!: string;
  endereco!: Endereco;

  formEndereco : FormGroup;

  constructor(private fb: FormBuilder) {
    this.enderecoService = inject(EnderecosService);
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
    this.idEndereco = this.route.snapshot.paramMap.get('id')!;
    this.enderecoService.obterEndereco(this.idEndereco)
        .subscribe(c => {
          this.endereco = c;
          this.formEndereco.patchValue(this.endereco);
        });
  }

  enderecoSalvoComSucessso(data: any){
    console.log('Endereço salvo com sucesso', data);
    this.router.navigate(['/clientes/detalhes', this.endereco.idCliente]);
  }

  erroAoSalvarEndereco(error: any){
    console.error('Erro ao salvar endereço', error);
    if(error.error && typeof error.error === 'object') {
      const formattedErrors = Object.entries(error.error)
        .map(([ _, message]) => `${message}`)
        .join('\n');

      alert('Erro ao salvar endereco:\n' + formattedErrors);
    } else {
      alert('Erro ao salvar endereco: ' + error.error);
    }
  }

   onSubmit() {
      const enderecoAtualizado : Endereco = this.formEndereco.value;
      enderecoAtualizado.id = this.endereco.id;
      this.enderecoService.atualizarEndereco(enderecoAtualizado).subscribe({
        next: (data) => this.enderecoSalvoComSucessso(data),
        error: (error) => this.erroAoSalvarEndereco(error)
      });     
    }
}
