export type Klink = {
  id: string;
  name: string;
  description: string | null;
  updatedAt: number;
  readKey: string | null;
  writeKey: string | null;
};

export type KlinkEntry = {
  value: string;
};
