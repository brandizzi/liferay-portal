import PortletBase from 'frontend-js-web/liferay/PortletBase.es';
import 'metal';
import 'metal-component';
import Soy from 'metal-soy';
import {Config} from 'metal-state';

import templates from './FieldMappings.soy';

class FieldMappings extends PortletBase {

	created() {

	}
}

FieldMappings.STATE = {

};

Soy.register(FieldMappings, templates);

export {FieldMappings};
export default FieldMappings;