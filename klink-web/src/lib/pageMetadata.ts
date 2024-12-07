/*
 * Open Web Standards define an 'Open Graph' og prefix to a tag inside the meta content of the page.
 * From here we can retrieve titles, descriptions, images etc.
 */

type PageMetadata = {
    title: string,
    description: string
}

export async function getPageMetadata(url: string): Promise<PageMetadata | null> {
    try {
        const response = await fetch(url);
        if (!response.ok) {
            return null;
        }
        const html = await response.text();

        const parser = new DOMParser();
        const doc = parser.parseFromString(html, "text/html");

        const title = parseTitle(doc);
        const description = parseDescription(doc);

        return {
            title: title,
            description: description
        }
    } catch (error) {
        return null;
    }
}

function parseTitle(doc: Document): string | null {
    const ogTitle = doc.querySelector('meta[property="og:title"]');
    if (ogTitle) {
        return ogTitle.getAttribute('content');
    }
    const xTitle = doc.querySelector('meta[name="twitter:title"]');
    if (xTitle) {
        return xTitle.getAttribute('content');
    }
    const h1 = doc.querySelector('h1').innerHTML;
    if (h1 && h1.length > 0) {
        return h1;
    }
    const h2 = doc.querySelector('h2').innerHTML;
    if (h2 && h2.length > 0) {
        return h2;
    }
    return null;
}

function parseDescription(doc: Document): string | null {
    const ogDesc = doc.querySelector('meta[property="og:description"]');
    if (ogDesc) {
        return ogDesc.getAttribute('content');
    }
    const xDesc = doc.querySelector('meta[name="twitter:description"]');
    if (xDesc) {
        return xDesc.getAttribute('content');
    }
    const metaDesc = doc.querySelector('meta[property="description"]');
    if (metaDesc) {
        return metaDesc.getAttribute('content');
    }
    return null;
}
