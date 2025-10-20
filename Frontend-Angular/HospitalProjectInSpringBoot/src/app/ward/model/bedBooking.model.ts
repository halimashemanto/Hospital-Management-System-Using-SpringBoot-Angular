export interface BedDTO {
  id?: number;
  bedNumber: string;
  occupied?: boolean;
  pricePerDay: number;
}

export interface WardDTO {
  id?: number;
  wardName: string;
  wardType: string;
  pricePerDay: number;
  beds?: BedDTO[];
  facilities?: FacilityDTO[];

}

export interface FacilityDTO {
  id?: number;
  name: string;
  description: string;
  isAvailable?: boolean;
}
