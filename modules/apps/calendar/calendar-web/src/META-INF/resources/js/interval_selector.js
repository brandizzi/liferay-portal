AUI.add(
	'liferay-calendar-interval-selector',
	function(A) {
		var AArray = A.Array;

		var EVENT_SELECTION_CHANGE = 'selectionChange';

		var IntervalSelector = A.Component.create(
			{
				AUGMENTS: [Liferay.PortletBase],

				EXTENDS: A.Base,

				NAME: 'interval-selector',

				prototype: {
					initializer: function(config) {
						var instance = this;

						instance.eventHandlers = [];

						instance._containerNode = instance.byId(config.containerId);
						instance._submitButtonNode = instance.byId(config.submitButtonId);

						instance._duration = 0;
						instance._endDate = new Date();
						instance._startDate = new Date();
						instance._validDate = true;

						instance._endDatePicker = instance._getComponent(config.endDatePickerName + 'DatePicker');
						instance._endTimePicker = instance._getComponent(config.endTimePickerName + 'TimePicker');
						instance._startDatePicker = instance._getComponent(config.startDatePickerName + 'DatePicker');
						instance._startTimePicker = instance._getComponent(config.startTimePickerName + 'TimePicker');

						instance._initPicker(instance._endDatePicker);
						instance._initPicker(instance._endTimePicker);
						instance._initPicker(instance._startDatePicker);
						instance._initPicker(instance._startTimePicker);

						instance._setEndDate();
						instance._setEndTime();
						instance._setStartDate();
						instance._setStartTime();
						instance._setDuration();

						instance.bindUI();
					},

					bindUI: function() {
						var instance = this;

						instance.eventHandlers.push(
							instance._endDatePicker.on(EVENT_SELECTION_CHANGE, instance._onEndDatePickerSelectionChange, instance),
							instance._endTimePicker.on(EVENT_SELECTION_CHANGE, instance._onEndTimePickerSelectionChange, instance),
							instance._startDatePicker.on(EVENT_SELECTION_CHANGE, instance._onStartDatePickerSelectionChange, instance),
							instance._startTimePicker.on(EVENT_SELECTION_CHANGE, instance._onStartTimePickerSelectionChange, instance)
						);
					},

					destructor: function() {
						var instance = this;

						AArray.invoke(instance.eventHandlers, 'detach');

						instance.eventHandlers = null;
					},

					_getComponent: function(name) {
						var instance = this;

						return Liferay.component(instance.NS + name);
					},

					_initPicker: function(picker) {
						var instance = this;

						var attrs = picker.getAttrs();

						var inputNode = A.one(attrs.container._node.children[0]);

						picker.useInputNodeOnce(inputNode);
					},

					_onEndDatePickerSelectionChange: function() {
						var instance = this;

						instance._setEndDate();

						if (instance._validDate && (instance._startDate.valueOf() >= instance._endDate.valueOf())) {
							instance._startDate = new Date(instance._endDate.valueOf() - instance._duration);

							instance._setStartDatePickerDate();
						}

						instance._setDuration();
						instance._validate();
					},

					_onEndTimePickerSelectionChange: function() {
						var instance = this;

						instance._setEndTime();

						if (instance._validDate && (instance._startDate.valueOf() >= instance._endDate.valueOf())) {
							instance._startDate = new Date(instance._endDate.valueOf() - instance._duration);

							instance._setStartDatePickerDate();
							instance._setStartTimePickerTime();
						}

						instance._setDuration();
						instance._validate();
					},

					_onStartDatePickerSelectionChange: function() {
						var instance = this;

						instance._setStartDate();

						if (instance._validDate) {
							instance._endDate = new Date(instance._startDate.valueOf() + instance._duration);

							instance._setEndDatePickerDate();
						}

						instance._setDuration();
						instance._validate();
					},

					_onStartTimePickerSelectionChange: function() {
						var instance = this;

						instance._setStartTime();

						if (instance._validDate) {
							instance._endDate = new Date(instance._startDate.valueOf() + instance._duration);

							instance._setEndDatePickerDate();
							instance._setEndTimePickerTime();
						}

						instance._setDuration();
						instance._validate();
					},

					_setDuration: function() {
						var instance = this;

						instance._duration = (instance._endDate.valueOf() - instance._startDate.valueOf());
					},

					_setEndDate: function() {
						var instance = this;

						var endDate = instance._endDatePicker.getDate();

						instance._endDate.setDate(endDate.getDate());
						instance._endDate.setMonth(endDate.getMonth());
						instance._endDate.setYear(endDate.getFullYear());
					},

					_setEndDatePickerDate: function() {
						var instance = this;

						instance._endDatePicker.clearSelection(true);

						instance._endDatePicker.selectDates([instance._endDate]);
					},

					_setEndTime: function() {
						var instance = this;

						var endTime = instance._endTimePicker.getTime();

						instance._endDate.setHours(endTime.getHours());
						instance._endDate.setMinutes(endTime.getMinutes());
					},

					_setEndTimePickerTime: function() {
						var instance = this;

						instance._endTimePicker.selectDates([instance._endDate]);
					},

					_setStartDate: function() {
						var instance = this;

						var startDate = instance._startDatePicker.getDate();

						instance._startDate.setDate(startDate.getDate());
						instance._startDate.setMonth(startDate.getMonth());
						instance._startDate.setYear(startDate.getFullYear());
					},

					_setStartDatePickerDate: function() {
						var instance = this;

						instance._startDatePicker.clearSelection(true);

						instance._startDatePicker.selectDates([instance._startDate]);
					},

					_setStartTime: function() {
						var instance = this;

						var startTime = instance._startTimePicker.getTime();

						instance._startDate.setHours(startTime.getHours());
						instance._startDate.setMinutes(startTime.getMinutes());
					},

					_setStartTimePickerTime: function() {
						var instance = this;

						instance._startTimePicker.selectDates([instance._startDate]);
					},

					_validate: function() {
						var instance = this;

						instance._validDate = (instance._duration > 0);

						var validDate = instance._validDate;

						var meetingEventDate = instance._containerNode;

						if (meetingEventDate) {
							meetingEventDate.toggleClass('error', !validDate);

							var helpInline = meetingEventDate.one('.help-inline');

							if (validDate && helpInline) {
								helpInline.remove();
							}

							if (!validDate && !helpInline) {
								var inlineHelp = A.Node.create('<div class="help-inline">' + Liferay.Language.get('the-end-time-must-be-after-the-start-time') + '</div>');

								meetingEventDate.insert(inlineHelp);
							}

							var submitButton = instance._submitButtonNode;

							if (submitButton) {
								submitButton.attr('disabled', !validDate);
							}
						}
					}
				}
			}
		);

		Liferay.IntervalSelector = IntervalSelector;
	},
	'',
	{
		requires: ['aui-base', 'liferay-portlet-base']
	}
);