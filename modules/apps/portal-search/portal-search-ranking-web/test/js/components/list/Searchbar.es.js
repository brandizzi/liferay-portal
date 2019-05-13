import React from 'react';
import SearchBar from 'components/list/SearchBar.es';
import {cleanup, fireEvent, render} from 'react-testing-library';
import 'jest-dom/extend-expect';
import {getMockResultsData} from 'test/mock-data.js';
import {resultsDataToMap} from 'utils/util.es';

const DATA_MAP = resultsDataToMap(
	getMockResultsData(
		10,
		0,
		'',
		false
	).documents
);

const DROPDOWN_TOGGLE_ID = 'dropdown-toggle';

describe(
	'SearchBar',
	() => {
		afterEach(cleanup);

		it(
			'should have an add result button when onAddResultSubmit is defined',
			() => {
				const {queryByText} = render(
					<SearchBar
						dataMap={DATA_MAP}
						onAddResultSubmit={jest.fn()}
						onClickHide={jest.fn()}
						onClickPin={jest.fn()}
						onSelectAll={jest.fn()}
						onSelectClear={jest.fn()}
						resultIds={[102, 104, 103]}
						selectedIds={[]}
					/>
				);

				expect(queryByText('Add a Result')).not.toBeNull();
			}
		);

		it(
			'should not have an add result button when onAddResultSubmit is not defined',
			() => {
				const {queryByText} = render(
					<SearchBar
						dataMap={DATA_MAP}
						onClickHide={jest.fn()}
						onClickPin={jest.fn()}
						onSelectAll={jest.fn()}
						onSelectClear={jest.fn()}
						resultIds={[102, 104, 103]}
						searchBarTerm={'test'}
						selectedIds={[]}
					/>
				);

				expect(queryByText('Add a Result')).toBeNull();
			}
		);

		it(
			'should show what is selected using selectedIds',
			() => {

				const {queryByText} = render(
					<SearchBar
						dataMap={DATA_MAP}
						onAddResultSubmit={jest.fn()}
						onClickHide={jest.fn()}
						onClickPin={jest.fn()}
						onSelectAll={jest.fn()}
						onSelectClear={jest.fn()}
						resultIds={[102, 104, 103]}
						selectedIds={[102, 103]}
					/>
				);

				expect(queryByText('2 of 3 Items Selected')).not.toBeNull();
				expect(queryByText('Add a Result')).toBeNull();
			}
		);

		it(
			'should show the dropdown when clicked on',
			() => {

				const {container, getByTestId} = render(
					<SearchBar
						dataMap={DATA_MAP}
						onAddResultSubmit={jest.fn()}
						onClickHide={jest.fn()}
						onClickPin={jest.fn()}
						onSelectAll={jest.fn()}
						onSelectClear={jest.fn()}
						resultIds={[102, 104, 103]}
						selectedIds={[102, 103]}
					/>
				);

				fireEvent.click(getByTestId(DROPDOWN_TOGGLE_ID));

				expect(container.querySelector('.dropdown-menu')).toHaveClass('show');
			}
		);

		it(
			'should show no items selected with empty selectedIds',
			() => {
				const {queryByText} = render(
					<SearchBar
						dataMap={DATA_MAP}
						onAddResultSubmit={jest.fn()}
						onClickHide={jest.fn()}
						onClickPin={jest.fn()}
						onSelectAll={jest.fn()}
						onSelectClear={jest.fn()}
						resultIds={[102, 104, 103]}
						selectedIds={[]}
					/>
				);

				expect(queryByText('Items Selected')).toBeNull();
				expect(queryByText('Add a Result')).not.toBeNull();
			}
		);
	}
);