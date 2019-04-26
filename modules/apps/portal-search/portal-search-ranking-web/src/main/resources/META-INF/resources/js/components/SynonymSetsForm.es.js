import ClayButton from 'components/shared/ClayButton.es';
import ClayMultiselect from 'components/shared/ClayMultiselect.es';
import PropTypes from 'prop-types';
import React, {Component} from 'react';

class SynonymSetsForm extends Component {
	static propTypes = {
		formName: PropTypes.string,
		inputName: PropTypes.string,
		onClickSubmit: PropTypes.func
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
		const form = document.forms[this.props.formName];
		const synonymSetsString = this.state.synonyms.map(s => s.value).toString();

		form.elements[this.props.inputName].value = synonymSetsString;
		form.submit();
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
				<div className="container-fluid-max-xl">
					<div className="sheet-lg">
						<div className="sheet-title">
							{Liferay.Language.get('create-synonym-set')}
						</div>

						<div className="sheet-text">
							{Liferay.Language.get('broaden-the-scope-of-search-by-treating-terms-equally-using-synonyms')}
						</div>

						<label>{Liferay.Language.get('synonyms')}</label>
						<ClayMultiselect
							onAction={this._handleUpdate}
							value={synonyms}
						/>

						<div className="form-feedback-group">
							<div className="form-text">
								{Liferay.Language.get('add-an-alias-instruction')}
							</div>
						</div>

						<div className="sheet-footer">
							<ClayButton
								disabled={
									synonyms.length === 0
								}
								displayStyle="primary"
								label={Liferay.Language.get('publish')}
								onClick={this._handleSubmit}
							/>
							<ClayButton
								label={Liferay.Language.get('save-as-draft')}
								onClick={this._handleSaveAsDraft}
							/>
							<ClayButton
								label={Liferay.Language.get('cancel')}
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
