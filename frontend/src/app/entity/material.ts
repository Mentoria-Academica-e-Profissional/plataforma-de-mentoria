export enum MaterialType {
  DOCUMENTO = "DOCUMENTO",
  VIDEO = "VIDEO",
  LINK = "LINK"
}

export interface Material {
  id: number;
  title: string;
  materialType: MaterialType;
  url?: string;
  filePath?: string;
  interestArea: string[];
  userUploaderId: number;
}
