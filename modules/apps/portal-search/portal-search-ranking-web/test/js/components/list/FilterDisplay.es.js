import React from 'react';
import FilterDisplay from 'components/list/FilterDisplay.es';
import {cleanup, fireEvent, render} from 'react-testing-library';
import 'jest-dom/extend-expect';

describe(
	'FilterDisplay',
	() => {
		afterEach(cleanup);

		it(
			'should have the correct description',
			() => {
				const {getByText} = render(
					<FilterDisplay
						onClear={jest.fn()}
						searchBarTerm={'example'}
						totalResultsCount={250}
					/>
				);

				expect(getByText('250 Results for example')).toBeInTheDocument();
			}
		);

		it(
			'should call the onClear function when clicking on Clear',
			() => {
				const onClear = jest.fn();

				const {getByText} = render(
					<FilterDisplay
						onClear={onClear}
						searchBarTerm={'example'}
						totalResultsCount={250}
					/>
				);

				fireEvent.click(getByText('Clear'));

				expect(onClear).toHaveBeenCalledTimes(1);
			}
		);
	}
);