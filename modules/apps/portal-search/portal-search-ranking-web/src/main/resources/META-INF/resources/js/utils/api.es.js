// @TODO Replace with real endpoint. i.e. /o/headless-search/v1.0/search

const DOCUMENT_API_BASE_URL = 'http://localhost:8080/o/headless-search/v1.0/search';

/**
 * Fetches documents.
 * @param {number} config.companyId
 * @param {number} config.size
 * @param {boolean} config.hidden
 * @param {string} config.searchIndex
 * @param {string} config.keywords
 * @param {number} config.from
 */
export function fetchDocuments(config) {
	const {companyId, size, hidden, keywords, searchIndex, from} = config;

	let url = `${DOCUMENT_API_BASE_URL}
		/${companyId}
		/${keywords}
		/${hidden}
		/${from}
		/${size}`;

	// @TODO Remove. This is for getting mocked hidden results.

	if (hidden) {
		url = 'http://www.mocky.io/v2/5cabd9ab3000002900103266';
	}

	return fetch(url)
		.then(response => response.json())
		.then(
			data => (
				{
					items: data.documents,
					total: data.total
				}
			)
		);
}