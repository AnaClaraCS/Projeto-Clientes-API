import { Endereco } from "./endereco.interface";

export interface Cliente {
    id: string;
    nome: string;
    cpf: string;
    email: string;
    dataNascimento: Date;
    enderecos: Endereco[];
}
