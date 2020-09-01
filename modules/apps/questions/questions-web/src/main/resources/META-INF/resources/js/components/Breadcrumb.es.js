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

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import React, {useCallback, useContext, useEffect, useState} from 'react';
import {withRouter} from 'react-router-dom';

import {AppContext} from '../AppContext.es';
import {
	client,
	getSectionByRootSection,
	getSectionQuery,
} from '../utils/client.es';
import {historyPushWithSlug, stringToSlug} from '../utils/utils.es';
import BreadcrumbDropdown from './BreadcrumbDropdown.es';
import Link from './Link.es';
import NewTopicModal from './NewTopicModal.es';

export default withRouter(({allowCreateTopicInRootTopic, history, section}) => {
	const context = useContext(AppContext);

	const rootTopicId = context.rootTopicId;

	const MAX_SECTIONS_IN_BREADCRUMB = 3;
	const historyPushParser = historyPushWithSlug(history.push);
	const [breadcrumbNodes, setBreadcrumbNodes] = useState([]);
	const [visible, setVisible] = useState(false);

	const getSubSections = (section) =>
		(section &&
			section.messageBoardSections &&
			section.messageBoardSections.items) ||
		[];

	const createEllipsisSectionData = () => {
		const categories = breadcrumbNodes
			.slice(1, breadcrumbNodes.length - 1)
			.map((section) => {
				return {title: section.title};
			});

		return {subCategories: categories, title: ''};
	};

	const findParent = (messageBoardSectionId) =>
		client
			.query({
				query: getSectionQuery,
				variables: {messageBoardSectionId},
			})
			.then(({data}) => data.messageBoardSection);

	const buildBreadcrumbNodesData = useCallback(
		(rootSection, section, acc = []) => {
			acc.push({
				subCategories: getSubSections(section),
				title: section.title,
			});
			if (+rootSection !== +section.id) {
				if (section.parentMessageBoardSectionId) {
					if (section.parentMessageBoardSection) {
						return Promise.resolve(
							buildBreadcrumbNodesData(
								rootSection,
								section.parentMessageBoardSection,
								acc
							)
						);
					}

					return findParent(
						section.parentMessageBoardSectionId
					).then((section) =>
						buildBreadcrumbNodesData(rootSection, section, acc)
					);
				}
			}

			return +rootSection === 0
				? Promise.resolve(
						getSectionByRootSection(
							context.siteKey,
							rootSection
						).then((data) => {
							acc.push({
								subCategories: data.messageBoardSections.items,
								title: rootSection,
							});

							return acc.reverse();
						})
				  ).then(acc)
				: Promise.resolve(acc.reverse());
		},
		[context.siteKey]
	);

	useEffect(() => {
		if (!section) {
			return;
		}

		buildBreadcrumbNodesData(rootTopicId, section).then((acc) =>
			setBreadcrumbNodes(acc)
		);
	}, [buildBreadcrumbNodesData, rootTopicId, section]);

	return (
		<section className="align-items-center d-flex mb-0 questions-breadcrumb">
			<ol className="breadcrumb m-0">
				{breadcrumbNodes.length > MAX_SECTIONS_IN_BREADCRUMB ? (
					<ShortenedBreadcrumb />
				) : (
					<AllBreadcrumb />
				)}
			</ol>
			{((section &&
				section.actions &&
				section.actions['add-subcategory']) ||
				allowCreateTopicInRootTopic) && (
				<>
					<NewTopicModal
						currentSectionId={section && section.id}
						onClose={() => setVisible(false)}
						onCreateNavigateTo={(topicName) =>
							historyPushParser(
								`/questions/${stringToSlug(topicName)}`
							)
						}
						visible={visible}
					/>
					<ClayButton
						className="breadcrumb-button c-ml-3 c-p-2"
						displayType="unstyled"
						onClick={() => setVisible(true)}
					>
						<ClayIcon className="c-mr-2" symbol="plus" />
						{Liferay.Language.get('new-topic')}
					</ClayButton>
				</>
			)}
		</section>
	);

	function AllBreadcrumb() {
		return (
			<>
				{context.showCardsForTopicNavigation ? (
					<>
						<li className="breadcrumb-item breadcrumb-text-truncate mr-0">
							<Link
								className="breadcrumb-item questions-breadcrumb-unstyled"
								to={'/'}
							>
								<ClayIcon symbol="home-full" />
							</Link>
						</li>
						<BreadcrumbNode
							showFirstNode={false}
							ui={<ClayIcon symbol="home-full" />}
						/>
					</>
				) : (
					<BreadcrumbNode ui={<ClayIcon symbol="home-full" />} />
				)}
			</>
		);
	}

	function ShortenedBreadcrumb() {
		return (
			<>
				{context.showCardsForTopicNavigation && (
					<li className="breadcrumb-item breadcrumb-text-truncate mr-0">
						<Link
							className="breadcrumb-item questions-breadcrumb-unstyled"
							to={'/'}
						>
							<ClayIcon symbol="home-full" />
						</Link>
					</li>
				)}
				<BreadcrumbNode
					end={1}
					showFirstNode={false}
					start={0}
					ui={<ClayIcon symbol="home-full" />}
				/>
				<li className="breadcrumb-item breadcrumb-text-truncate mr-0">
					<BreadcrumbDropdown
						className="breadcrumb-item breadcrumb-text-truncate"
						createLink={false}
						section={createEllipsisSectionData()}
					/>
				</li>
				<BreadcrumbNode showFirstNode={true} start={-1} />
			</>
		);
	}

	function BreadcrumbNode({
		end = breadcrumbNodes.length,
		showFirstNode = true,
		start = 0,
		ui,
	}) {
		return breadcrumbNodes
			.filter((node) => node.title)
			.slice(start, end)
			.map((section, i) => (
				<>
					{section.subCategories.length <= 0 ? (
						<li
							className="breadcrumb-item breadcrumb-text-truncate mr-0"
							key={i}
						>
							{section.title}
						</li>
					) : !context.showCardsForTopicNavigation && i === 0 ? (
						<li
							className="breadcrumb-item breadcrumb-text-truncate mr-0"
							key={i}
						>
							<BreadcrumbDropdown
								className="breadcrumb-item breadcrumb-text-truncate"
								section={section}
								ui={ui}
							/>
						</li>
					) : showFirstNode && i === 0 ? (
						<li
							className="breadcrumb-item breadcrumb-text-truncate mr-0"
							key={i}
						>
							<BreadcrumbDropdown
								className="breadcrumb-item breadcrumb-text-truncate"
								section={section}
							/>
						</li>
					) : (
						i !== 0 && (
							<li
								className="breadcrumb-item breadcrumb-text-truncate mr-0"
								key={i}
							>
								<BreadcrumbDropdown
									className="breadcrumb-item breadcrumb-text-truncate"
									section={section}
								/>
							</li>
						)
					)}
				</>
			));
	}
});
