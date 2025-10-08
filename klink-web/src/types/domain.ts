export type Klink = {
  id: string;
  name: string;
  description: string | null;
  updatedAt: Date,
  readKey: string | null;
  writeKey: string | null;
};

export type KlinkEntry = {
  value: string;
  title?: string;
  description?: string;
};

export type KlinkMetadata = {
  isShared: boolean;
  isReadOnly: boolean;
  isEditable: boolean;
};

export type KlinkModel = Klink & KlinkMetadata;

export type KlinkChangeEvent = {
  operation: "updated" | "inserted" | "deleted";
  entries: {
    value: string;
    createdAt: any;
    title?: string;
    description?: string;
  }[];
};

export function klinkMetadata(klink: Klink): KlinkMetadata {
  return {
    isShared: !!klink.readKey,
    isReadOnly: klink.readKey && !klink.writeKey,
    isEditable: !!klink.readKey && !!klink.writeKey,
  };
}

export function klinkModel(klink: Klink): KlinkModel {
  return {
    id: klink.id,
    name: klink.name,
    description: klink.description,
    updatedAt: klink.updatedAt,
    readKey: klink.readKey,
    writeKey: klink.writeKey,
    isShared: !!klink.readKey,
    isReadOnly: klink.readKey && !klink.writeKey,
    isEditable: !!klink.readKey && !!klink.writeKey,
  };
}

export function isUrl(value: string): boolean {
  try {
    const url = new URL(value);
    return ["http:", "https:"].includes(url.protocol);
  } catch (_) {
    return false;
  }
}
