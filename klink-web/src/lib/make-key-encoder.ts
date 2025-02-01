import { KlinkEncoder } from "./make-encoder";

export interface KeyEncoder {
    encode(value: { readKey: string, writeKey?: string }): string
    decode(value: string): { readKey: string, writeKey?: string }
}

export function makeKeyEncoder(encoder: KlinkEncoder): KeyEncoder {
    return {
        encode: function(value: { readKey: string; writeKey?: string; }): string {
            const temp = value.readKey + (value.writeKey ?? "");
            return encoder.encode(temp);
        },
        decode: function(value: string): { readKey: string; writeKey?: string; } {
            const decoded = encoder.decode(value);
            if (decoded.length === 8) {
                return {
                    readKey: decoded
                }
            }
            return {
                readKey: decoded.slice(0, 8),
                writeKey: decoded.slice(8)
            }
        }
    }
}
