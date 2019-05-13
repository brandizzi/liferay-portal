import ClayButton from 'components/shared/ClayButton.es';
import React, {Component} from 'react';
import {PropTypes} from 'prop-types';
import {sub} from 'utils/language.es';

class FilterDisplay extends Component {
	static propTypes = {
		onClear: PropTypes.func,
		searchBarTerm: PropTypes.string,
		totalResultsCount: PropTypes.number
	}

	render() {
		const {onClear, searchBarTerm, totalResultsCount} = this.props;

		return (
			<nav className="tbar tbar-inline-xs-down subnav-tbar subnav-tbar-primary">
				<div className="container-fluid container-fluid-max-xl">
					<ul className="tbar-nav tbar-nav-wrap">
						<li className="tbar-item tbar-item-expand">
							<div className="tbar-section">
								<span className="component-text text-truncate-inline">
									<span className="text-truncate">
										{sub(
											Liferay.Language.get(
												'x-results-for-x'
											),
											[
												totalResultsCount,
												searchBarTerm
											]
										)}
									</span>
								</span>
							</div>
						</li>

						<li className="tbar=item">
							<div className="tbar-section">
								<ClayButton
									borderless
									displayStyle="unstyled"
									label={Liferay.Language.get(
										'clear'
									)}
									onClick={onClear}
									size="sm"
									title={Liferay.Language.get(
										'clear'
									)}
								/>
							</div>
						</li>
					</ul>
				</div>
			</nav>
		);
	}
}

export default FilterDisplay;