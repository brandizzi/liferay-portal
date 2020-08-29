/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import {VIEWPORT_SIZES} from '../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {StoreAPIContextProvider} from '../../../../../../../src/main/resources/META-INF/resources/page_editor/app/store/index';
import updateItemConfig from '../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig';
import {ContainerStylesPanel} from '../../../../../../../src/main/resources/META-INF/resources/page_editor/plugins/page-structure/components/item-configuration-panels/ContainerStylesPanel';

const renderComponent = ({
	dispatch = () => {},
	selectedViewportSize = VIEWPORT_SIZES.desktop,
} = {}) =>
	render(
		<StoreAPIContextProvider
			dispatch={dispatch}
			getState={() => ({
				segmentsExperienceId: '0',
				selectedViewportSize,
			})}
		>
			<ContainerStylesPanel
				item={{
					children: [],
					config: {tablet: {styles: {}}},
					itemId: '0',
					parentId: '',
					type: '',
				}}
			/>
		</StoreAPIContextProvider>
	);

jest.mock(
	'../../../../../../../src/main/resources/META-INF/resources/page_editor/app/thunks/updateItemConfig',
	() => jest.fn()
);

jest.mock(
	'../../../../../../../src/main/resources/META-INF/resources/page_editor/app/config',
	() => ({
		config: {
			availableViewportSizes: {
				desktop: {label: 'Desktop', sizeId: 'desktop'},
				mobile: {label: 'Mobile', sizeId: 'mobile'},
				tablet: {label: 'Tablet', sizeId: 'tablet'},
			},
			commonStyles: [
				{
					label: 'margin',
					styles: [
						{
							dataType: 'string',
							defaultValue: '0',
							dependencies: [],
							displaySize: 'small',
							label: 'margin-top',
							name: 'marginTop',
							responsive: true,
							responsiveTemplate: 'mt{viewport}{value}',
							type: 'select',
							validValues: [
								{
									label: '0',
									value: '0',
								},
								{
									label: '1',
									value: '1',
								},
							],
						},
					],
				},
			],
			defaultSegmentsExperienceId: 0,
			marginOptions: [],
			paddingOptions: [],
		},
	})
);

describe('ContainerStylesPanel', () => {
	afterEach(() => {
		cleanup();
		updateItemConfig.mockClear();
	});

	it('renders correctly', () => {
		const {getByLabelText} = renderComponent();

		expect(getByLabelText('container-width')).toBeInTheDocument();
	});

	it('calls dispatch method when changing the container width', async () => {
		const {getByLabelText} = renderComponent();

		const containerWidthSelect = getByLabelText('container-width');

		await fireEvent.change(containerWidthSelect, {
			target: {value: 'fixed'},
		});

		expect(updateItemConfig).toBeCalledWith(
			expect.objectContaining({
				itemConfig: {
					widthType: 'fixed',
				},
			})
		);
	});

	it('allows changing styles for a given viewport', async () => {
		const {getByLabelText} = renderComponent({
			selectedViewportSize: 'tablet',
		});
		const input = getByLabelText('margin-top');

		await fireEvent.change(input, {
			target: {value: '1'},
		});

		expect(updateItemConfig).toHaveBeenCalledWith({
			itemConfig: {
				tablet: {
					styles: {
						marginTop: '1',
					},
				},
			},
			itemId: '0',
			segmentsExperienceId: '0',
		});
	});
});
