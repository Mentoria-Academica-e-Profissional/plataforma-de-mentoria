import { User } from './user';

export interface Mentor extends User {
    cpf: string;
    birthDate: Date;
    course: string;
    professionalSummary: string;
    affiliationType: string;
    specializations: string[];
    interestArea: string;
}

