export interface Material {
  id?: number;
  title: string;
  materialType: MaterialType;
  url?: string;
  filePath?: string;
  interestArea: InterestArea[];
  userUploaderId?: number;
  createdAt?: Date;
  updatedAt?: Date;
}

export enum MaterialType {
  DOCUMENTO = 'DOCUMENTO',
  VIDEO = 'VIDEO',
  LINK = 'LINK'
}


export enum InterestArea {
  TECNOLOGIA_DA_INFORMACAO = 'TECNOLOGIA_DA_INFORMACAO',
  DESENVOLVIMENTO_DE_SOFTWARE = 'DESENVOLVIMENTO_DE_SOFTWARE',
  CIENCIA_DE_DADOS_E_IA = 'CIENCIA_DE_DADOS_E_IA',
  CIBERSEGURANCA = 'CIBERSEGURANCA',
  UX_UI_DESIGN = 'UX_UI_DESIGN',
  ENGENHARIA_GERAL = 'ENGENHARIA_GERAL',
  ENGENHARIA_CIVIL = 'ENGENHARIA_CIVIL',
  ENGENHARIA_DE_PRODUCAO = 'ENGENHARIA_DE_PRODUCAO',
  MATEMATICA_E_ESTATISTICA = 'MATEMATICA_E_ESTATISTICA',
  FISICA = 'FISICA',
  ADMINISTRACAO_E_GESTAO = 'ADMINISTRACAO_E_GESTAO',
  EMPREENDEDORISMO_E_INOVACAO = 'EMPREENDEDORISMO_E_INOVACAO',
  FINANCAS_E_CONTABILIDADE = 'FINANCAS_E_CONTABILIDADE',
  RECURSOS_HUMANOS = 'RECURSOS_HUMANOS',
  LOGISTICA_E_CADEIA_DE_SUPRIMENTOS = 'LOGISTICA_E_CADEIA_DE_SUPRIMENTOS',
  MARKETING_E_COMUNICACAO = 'MARKETING_E_COMUNICACAO',
  MARKETING_DIGITAL = 'MARKETING_DIGITAL',
  JORNALISMO = 'JORNALISMO',
  PUBLICIDADE_E_PROPAGANDA = 'PUBLICIDADE_E_PROPAGANDA',
  COMUNICACAO_INSTITUCIONAL = 'COMUNICACAO_INSTITUCIONAL',
  CIENCIAS_BIOLOGICAS_E_SAUDE = 'CIENCIAS_BIOLOGICAS_E_SAUDE',
  MEDICINA = 'MEDICINA',
  PSICOLOGIA = 'PSICOLOGIA',
  NUTRICAO = 'NUTRICAO',
  BIOTECNOLOGIA = 'BIOTECNOLOGIA',
  EDUCACAO = 'EDUCACAO',
  CIENCIAS_HUMANAS_E_SOCIAIS = 'CIENCIAS_HUMANAS_E_SOCIAIS',
  LETRAS = 'LETRAS',
  HISTORIA = 'HISTORIA',
  GEOGRAFIA = 'GEOGRAFIA',
  SOCIOLOGIA = 'SOCIOLOGIA',
  JURIDICO = 'JURIDICO',
  DIREITO_DIGITAL = 'DIREITO_DIGITAL',
  MEIO_AMBIENTE_E_SUSTENTABILIDADE = 'MEIO_AMBIENTE_E_SUSTENTABILIDADE'
}

export const InterestAreaLabels: Record<InterestArea, string> = {
  [InterestArea.TECNOLOGIA_DA_INFORMACAO]: 'Tecnologia da Informação',
  [InterestArea.DESENVOLVIMENTO_DE_SOFTWARE]: 'Desenvolvimento de Software',
  [InterestArea.CIENCIA_DE_DADOS_E_IA]: 'Ciência de Dados e IA',
  [InterestArea.CIBERSEGURANCA]: 'Cibersegurança',
  [InterestArea.UX_UI_DESIGN]: 'UX/UI Design',
  [InterestArea.ENGENHARIA_GERAL]: 'Engenharia Geral',
  [InterestArea.ENGENHARIA_CIVIL]: 'Engenharia Civil',
  [InterestArea.ENGENHARIA_DE_PRODUCAO]: 'Engenharia de Produção',
  [InterestArea.MATEMATICA_E_ESTATISTICA]: 'Matemática e Estatística',
  [InterestArea.FISICA]: 'Física',
  [InterestArea.ADMINISTRACAO_E_GESTAO]: 'Administração e Gestão',
  [InterestArea.EMPREENDEDORISMO_E_INOVACAO]: 'Empreendedorismo e Inovação',
  [InterestArea.FINANCAS_E_CONTABILIDADE]: 'Finanças e Contabilidade',
  [InterestArea.RECURSOS_HUMANOS]: 'Recursos Humanos',
  [InterestArea.LOGISTICA_E_CADEIA_DE_SUPRIMENTOS]: 'Logística e Cadeia de Suprimentos',
  [InterestArea.MARKETING_E_COMUNICACAO]: 'Marketing e Comunicação',
  [InterestArea.MARKETING_DIGITAL]: 'Marketing Digital',
  [InterestArea.JORNALISMO]: 'Jornalismo',
  [InterestArea.PUBLICIDADE_E_PROPAGANDA]: 'Publicidade e Propaganda',
  [InterestArea.COMUNICACAO_INSTITUCIONAL]: 'Comunicação Institucional',
  [InterestArea.CIENCIAS_BIOLOGICAS_E_SAUDE]: 'Ciências Biológicas e Saúde',
  [InterestArea.MEDICINA]: 'Medicina',
  [InterestArea.PSICOLOGIA]: 'Psicologia',
  [InterestArea.NUTRICAO]: 'Nutrição',
  [InterestArea.BIOTECNOLOGIA]: 'Biotecnologia',
  [InterestArea.EDUCACAO]: 'Educação',
  [InterestArea.CIENCIAS_HUMANAS_E_SOCIAIS]: 'Ciências Humanas e Sociais',
  [InterestArea.LETRAS]: 'Letras',
  [InterestArea.HISTORIA]: 'História',
  [InterestArea.GEOGRAFIA]: 'Geografia',
  [InterestArea.SOCIOLOGIA]: 'Sociologia',
  [InterestArea.JURIDICO]: 'Jurídico',
  [InterestArea.DIREITO_DIGITAL]: 'Direito Digital',
  [InterestArea.MEIO_AMBIENTE_E_SUSTENTABILIDADE]: 'Meio Ambiente e Sustentabilidade'
};