import { User } from './user';

export interface Mentored extends User {
    cpf: string;
    birthDate: Date;
    course: string;
    academicSummary: string;
    interestArea: string;
}

