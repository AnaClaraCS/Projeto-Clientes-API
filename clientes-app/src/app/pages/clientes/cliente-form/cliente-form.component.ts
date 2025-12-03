import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NgbAlertModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { FormBuilder, FormGroup, ReactiveFormsModule, FormsModule, Validators,  } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-cliente-form',
  imports: [ 
    CommonModule, 
    NgbDatepickerModule, 
    NgbAlertModule, 
    ReactiveFormsModule, 
    FormsModule, 
    NgxMaskDirective ],
  templateUrl: './cliente-form.component.html',
  styleUrl: './cliente-form.component.css',
  providers: [provideNgxMask()]
})
export class ClienteFormComponent {
  @Input() formCliente!: FormGroup;
  @Output() submitForm = new EventEmitter<FormGroup>();

  constructor(private fb: FormBuilder) {
    // Se o pai não passar um form, cria um default
    if (!this.formCliente) {
      this.formCliente = this.fb.group({
        nome: ['', Validators.required, Validators.minLength(8), Validators.maxLength(100)],
        cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)], Validators.minLength(11), Validators.maxLength(11)],
        email: ['', [Validators.required, Validators.email]],
        dataNascimento: [null, Validators.required],
      });
    }
  }

  onSubmit() {
    if (this.formCliente.valid) {
      this.submitForm.emit(this.formCliente);
    } else {
      this.formCliente.markAllAsTouched();
    }
  }
}
