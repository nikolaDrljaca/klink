import { readClipboard } from "@solid-primitives/clipboard";

export async function readLastClipboardEntry(): Promise<string> {
  return readClipboard()
    .then((it) => it[it.length - 1])
    .then((it) => it.getType("text/plain"))
    .then((it) => it.text());
}
