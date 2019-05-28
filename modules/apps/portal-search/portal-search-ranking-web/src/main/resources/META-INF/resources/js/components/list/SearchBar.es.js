import AddResult from 'components/add_result/index.es';
import ClayButton from 'components/shared/ClayButton.es';
import getCN from 'classnames';
import ItemDropdown from './ItemDropdown.es';
import React, {Component} from 'react';
import {PropTypes} from 'prop-types';
import {sub} from 'utils/language.es';

class SearchBar extends Component {
	static propTypes = {

		/**
		 * The data map of id to object it represents. Search bar needs to know
		 * about the dataMap to determine which actions are allowed for the
		 * selected items.
		 */
		dataMap: PropTypes.object.isRequired,
		disableSearch: PropTypes.bool,
		fetchDocumentsUrl: PropTypes.string,
		onAddResultSubmit: PropTypes.func,
		onClickHide: PropTypes.func,
		onClickPin: PropTypes.func,
		onRemoveSelect: PropTypes.func,
		onSearchBarEnter: PropTypes.func,
		onSelectAll: PropTypes.func.isRequired,
		onSelectClear: PropTypes.func.isRequired,
		onUpdateSearchBarTerm: PropTypes.func,
		resultIds: PropTypes.arrayOf(String),
		searchBarTerm: PropTypes.string,
		selectedIds: PropTypes.arrayOf(String)
	};

	static defaultProps = {
		disableSearch: false,
		resultIds: [],
		selectedIds: []
	};

	selectAllCheckbox = React.createRef();

	/**
	 * Sets the indeterminate state of the select all checkbox.
	 */
	componentDidUpdate() {
		const {resultIds, selectedIds} = this.props;

		const indeterminate = selectedIds.length > 0 &&
			selectedIds.length !== resultIds.length;

		this.selectAllCheckbox.current.indeterminate = indeterminate;
	}

	_handleAllCheckbox = () => {
		if (this.props.selectedIds.length > 0) {
			this.props.onSelectClear();
		}
		else {
			this.props.onSelectAll();
		}
	};

	_handleClickHide = () => {
		this.props.onRemoveSelect(this.props.selectedIds);

		this.props.onClickHide(this.props.selectedIds, !this._isAnyHidden());
	};

	_handleClickPin = () => {
		const {dataMap, onClickPin, onRemoveSelect, selectedIds} = this.props;

		const unpinnedIds = selectedIds.filter(id => !dataMap[id].pinned);

		if (unpinnedIds.length) {
			onRemoveSelect(selectedIds.filter(id => dataMap[id].hidden));

			onClickPin(unpinnedIds, true);
		}
		else {
			onRemoveSelect(selectedIds.filter(id => dataMap[id].addedResult));

			onClickPin(selectedIds, false);
		}
	};

	_handleSearchChange = event => {
		event.preventDefault();

		this.props.onUpdateSearchBarTerm(event.target.value);
	};

	_handleSearchEnter = () => {
		this.props.onSearchBarEnter();
	};

	_handleSearchKeyDown = event => {
		if (event.key === 'Enter' && event.currentTarget.value.trim()) {
			this._handleSearchEnter();
		}
	};

	/**
	 * Checks if there are any items selected.
	 * @returns {boolean} True if there is at least 1 item selected.
	 */
	_hasSelectedIds = () => this.props.selectedIds.length > 0;

	/**
	 * Checks if any selected ids contain any added items.
	 * @returns {boolean} True if any of the selected ids were added.
	 */
	_isAnyAddedResult = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => dataMap[id].addedResult);
	};

	/**
	 * Checks if any selected ids contain any hidden items.
	 * @returns {boolean} True if any selected ids are currently hidden.
	 */
	_isAnyHidden = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => dataMap[id].hidden);
	};

	/**
	 * Checks if any selected ids contain any unpinned items.
	 * @returns {boolean} True if any selected ids are currently unpinned.
	 */
	_isAnyUnpinned = () => {
		const {dataMap, selectedIds} = this.props;

		return selectedIds.some(id => !dataMap[id].pinned);
	};

	render() {
		const {
			disableSearch,
			fetchDocumentsUrl,
			onAddResultSubmit,
			resultIds,
			searchBarTerm,
			selectedIds
		} = this.props;

		const classManagementBar = getCN(
			'management-bar',
			this._hasSelectedIds() ?
				'management-bar-primary' :
				'management-bar-light',
			'navbar',
			'navbar-expand-md',
			'search-bar-root'
		);

		return (
			<nav className={classManagementBar}>
				<div className="container-fluid container-fluid-max-xl">
					<div className="navbar-form navbar-form-autofit navbar-overlay">
						<ul className="navbar-nav">
							<li className="nav-item">
								<div className="custom-control custom-checkbox">
									<label>
										<input
											aria-label={Liferay.Language.get('select-all')}
											checked={this._hasSelectedIds()}
											className="custom-control-input"
											disabled={!resultIds.length}
											onChange={this._handleAllCheckbox}
											ref={this.selectAllCheckbox}
											type="checkbox"
										/>

										<span className="custom-control-label" />
									</label>
								</div>
							</li>
						</ul>

						{this._hasSelectedIds() &&
							<React.Fragment>
								<ul className="navbar-nav navbar-nav-expand">
									<li className="nav-item">
										<span className="navbar-text">
											<strong>
												{sub(
													Liferay.Language.get(
														'x-of-x-items-selected'
													),
													[
														selectedIds.length,
														resultIds.length
													]
												)}
											</strong>
										</span>
									</li>
								</ul>

								<ul className="navbar-nav">
									<li className="nav-item">
										{!this._isAnyAddedResult() && (
											<div className="nav-link nav-link-monospaced">
												<ClayButton
													borderless
													className="component-action"
													iconName={
														this._isAnyHidden() ?
															'view' :
															'hidden'
													}
													onClick={this._handleClickHide}
													title={
														this._isAnyUnpinned() ?
															Liferay.Language.get('show-result') :
															Liferay.Language.get('hide-result')
													}
												/>
											</div>
										)}
									</li>

									<li className="nav-item">
										<div className="nav-link nav-link-monospaced">
											<ClayButton
												borderless
												className="component-action"
												iconName={
													this._isAnyUnpinned() ?
														'pin' :
														'unpin'
												}
												onClick={this._handleClickPin}
												title={
													this._isAnyUnpinned() ?
														Liferay.Language.get('pin-result') :
														Liferay.Language.get('unpin-result')
												}
											/>
										</div>
									</li>

									<li className="nav-item">
										<div className="nav-link nav-link-monospaced">
											<ItemDropdown
												addedResult={this._isAnyAddedResult()}
												hidden={this._isAnyHidden()}
												itemCount={selectedIds.length}
												onClickHide={this._handleClickHide}
												onClickPin={this._handleClickPin}
												pinned={!this._isAnyUnpinned()}
											/>
										</div>
									</li>
								</ul>
							</React.Fragment>
						}

						{!this._hasSelectedIds() &&
							<React.Fragment>
								<div className="navbar-nav navbar-nav-expand">
									<div className="container-fluid container-fluid-max-xl">
										<div className="input-group">
											<div className="input-group-item">
												<input
													aria-label={Liferay.Language.get('search')}
													className="form-control input-group-inset input-group-inset-after"
													disabled={disableSearch}
													onChange={this._handleSearchChange}
													onKeyDown={this._handleSearchKeyDown}
													placeholder={Liferay.Language.get('contains-text')}
													type="text"
													value={searchBarTerm}
												/>

												<div className="input-group-inset-item input-group-inset-item-after">
													<ClayButton
														displayStyle={'unstyled'}
														iconName="search"
														onClick={this._handleSearchEnter}
														title={Liferay.Language.get('search-icon')}
													/>
												</div>
											</div>
										</div>
									</div>
								</div>

								{onAddResultSubmit && (
									<ul className="navbar-nav">
										<AddResult
											fetchDocumentsUrl={fetchDocumentsUrl}
											onAddResultSubmit={onAddResultSubmit}
										/>
									</ul>
								)}
							</React.Fragment>
						}
					</div>
				</div>
			</nav>
		);
	}
}

export default SearchBar;