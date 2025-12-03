import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { ControlContainer, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbAlertModule } from '@ng-bootstrap/ng-bootstrap';
import { NgxMaskDirective, provideNgxMask } from 'ngx-mask';

@Component({
  selector: 'app-form-endereco',
  imports: [ CommonModule, NgbAlertModule, ReactiveFormsModule, FormsModule, NgxMaskDirective ],
  templateUrl: './form-endereco.component.html',
  styleUrl: './form-endereco.component.css',
  providers: [provideNgxMask()]
})
export class FormEnderecoComponent{
  @Input()
  formEndereco!: FormGroup;
}
