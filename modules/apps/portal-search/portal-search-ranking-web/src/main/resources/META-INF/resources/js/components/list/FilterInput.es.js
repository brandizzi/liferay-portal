import ClayButton from 'components/shared/ClayButton.es';
import React, {Component} from 'react';
import {PropTypes} from 'prop-types';

class FilterInput extends Component {
	static propTypes = {
		disableSearch: PropTypes.bool,
		onChange: PropTypes.func,
		onSubmit: PropTypes.func,
		searchBarTerm: PropTypes.string
	}
	static defaultProps = {
		disableSearch: false
	};

	_handleChange = event => {
		event.preventDefault();

		this.props.onChange(event.target.value);
	};

	_handleKeyDown = event => {
		if (event.key === 'Enter' && event.currentTarget.value.trim()) {
			this.props.onSubmit();
		}
	};

	render() {
		const {disableSearch, onSubmit, searchBarTerm} = this.props;

		return (
			<div className="navbar-nav navbar-nav-expand">
				<div className="container-fluid container-fluid-max-xl">
					<div className="input-group">
						<div className="input-group-item">
							<input
								aria-label={Liferay.Language.get('search')}
								className="form-control input-group-inset input-group-inset-after"
								disabled={disableSearch}
								onChange={this._handleChange}
								onKeyDown={this._handleKeyDown}
								placeholder={Liferay.Language.get('contains-text')}
								type="text"
								value={searchBarTerm}
							/>

							<div className="input-group-inset-item input-group-inset-item-after">
								<ClayButton
									displayStyle={'unstyled'}
									iconName="search"
									onClick={onSubmit}
									title={Liferay.Language.get('search-icon')}
								/>
							</div>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default FilterInput;