import React, {Component} from 'react';
import ClayButton from './ClayButton.es';
import {getLang} from 'utils/language.es';
import PropTypes from 'prop-types';
import ReactSelectTags from './ReactSelectTags.es';

class SynonymSetsForm extends Component {
	static propTypes = {
		onClickSubmit: PropTypes.func,
	};

	state = {
		synonyms: []
	};

	_handleCancel = () => {
		window.history.back();
	};

	_handleSaveAsDraft = () => {
		/* TODO: Call backend to save as draft synonym sets */
	};

	_handleSubmit = () => {
		/* TODO: Call backend to save synonym sets */
	};

	_handleUpdate = value => {
		this.setState({
			synonyms: value
		});
	};

	render() {
		const {synonyms} = this.state;

		return (
			<div className="synonym-sets-form">
				<div className="container-fluid container-fluid-max-xl container-form-lg">
					<div className="sheet sheet-lg">
						<div className="sheet-title">
							{getLang('create-synonym-set')}
						</div>

						<div className="sheet-text">
							{getLang('broaden-the-scope-of-search-by-treating-terms-equally-using-synonyms')}
						</div>

						<label>{getLang('synonyms')}</label>
						<ReactSelectTags
							value={synonyms}
							onAction={this._handleUpdate}
						/>

						<div className="form-feedback-group">
							<div className="form-text">
								{getLang('add-an-alias-instruction')}
							</div>
						</div>

						<div className="sheet-footer">
							<ClayButton
								disabled={
									synonyms.length === 0
								}
								displayStyle="primary"
								label={getLang('publish')}
								onClick={this._handleSubmit}
							/>
							<ClayButton
								label={getLang('save-as-draft')}
								onClick={this._handleSaveAsDraft}
							/>
							<ClayButton
								label={getLang('cancel')}
								onClick={this._handleCancel}
							/>
						</div>
					</div>
				</div>
			</div>
		);
	}
}

export default SynonymSetsForm;
