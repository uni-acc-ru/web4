export interface Point {
  id?: number;
  x: number;
  y: number;
  r: number;
  hit: boolean;
  executionTime?: number;
  timestamp?: string;
}

export interface PointRequest {
  x: number;
  y: number;
  r: number;
}
