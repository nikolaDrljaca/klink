
//Uses base64 adjusted to be safe for URLs
export interface KlinkEncoder {
    encode(value: string): string
    decode(value: string): string
}

const impl: KlinkEncoder = {
    encode: function(value: string): string {
        return btoa(value)
            .replace(/\+/g, '-')
            .replace(/\//g, '_')
            .replace(/=+$/, '');
    },
    decode: function(value: string): string {
        let padded = value + '='.repeat((4 - value.length % 4) % 4); // Fix missing padding
        padded = padded.replace(/-/g, '+').replace(/_/g, '/'); // Revert to standard Base64
        return atob(padded);
    }
}

export function makeEncoder(): KlinkEncoder {
    return impl;
}
