import { Klink } from "~/types/domain";

export const klinkEntryForageKey = (id: string) => `klink-items-${id}`;

export const isSharedEditable = (klink: Klink) =>
  !!klink.readKey && !!klink.writeKey;

export const isShared = (klink: Klink) => !!klink.readKey;
