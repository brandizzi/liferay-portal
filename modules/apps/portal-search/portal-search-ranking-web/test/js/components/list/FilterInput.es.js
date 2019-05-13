import React from 'react';
import FilterInput from 'components/list/FilterInput.es';
import {cleanup, fireEvent, render} from 'react-testing-library';
import 'jest-dom/extend-expect';

describe(
	'FilterInput',
	() => {
		afterEach(cleanup);

		it(
			'should have the searchbar term in the filter input',
			() => {
				const {getByPlaceholderText} = render(
					<FilterInput
						disableSearch={false}
						onChange={jest.fn()}
						onSubmit={jest.fn()}
						searchBarTerm={'test'}
					/>
				);

				const input = getByPlaceholderText('Contains Text');

				expect(input.value).toEqual('test');
			}
		);

		it(
			'should call the onChange function when adding to the input',
			() => {
				const onChange = jest.fn();

				const {queryByPlaceholderText} = render(
					<FilterInput
						disableSearch={false}
						onChange={onChange}
						onSubmit={jest.fn()}
						searchBarTerm={'test'}
					/>
				);

				const input = queryByPlaceholderText('Contains Text');

				fireEvent.change(input, {target: {value: 'a'}});

				expect(onChange).toHaveBeenCalledTimes(1);
			}
		);

		it(
			'should call the onLoadResults function when the searchbar enter is pressed',
			() => {
				const onSubmit = jest.fn();

				const {queryByPlaceholderText} = render(
					<FilterInput
						disableSearch={false}
						onChange={jest.fn()}
						onSubmit={onSubmit}
						searchBarTerm={'test'}
					/>
				);

				const input = queryByPlaceholderText('Contains Text');

				fireEvent.change(input, {target: {value: 'test'}});

				fireEvent.keyDown(input, {key: 'Enter',
					keyCode: 13,
					which: 13
				});

				expect(onSubmit).toHaveBeenCalledTimes(1);
			}
		);
	}
);