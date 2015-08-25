AUI.add(
	'liferay-calendar-date-picker-util',
	function(A) {
		var Lang = A.Lang;

		var toInt = Lang.toInt;

		Liferay.DatePickerUtil = {
			syncUI: function(form, fieldName, date) {
				var instance = this;

				var amPmNode = form.one('select[name$=' + fieldName + 'AmPm]');
				var hourNode = form.one('select[name$=' + fieldName + 'Hour]');
				var minuteNode = form.one('select[name$=' + fieldName + 'Minute]');

				var datePicker = Liferay.component(Liferay.CalendarUtil.PORTLET_NAMESPACE + fieldName + 'datePicker');

				if (datePicker) {
					datePicker.calendar.deselectDates();
					datePicker.calendar.selectDates(date);

					datePicker.syncUI();
				}

				var hours = date.getHours();
				var minutes = date.getMinutes();

				var amPm = hours < 12 ? 0 : 1;

				if (amPm === 1) {
					hours -= 12;

					if (hours === 12) {
						hours = 0;
					}
				}

				amPmNode.val(amPm);
				hourNode.val(hours);
				minuteNode.val(minutes);
			},

			linkToSchedulerEvent: function(datePickerContainer, schedulerEvent, dateAttr) {
				var instance = this;

				var selects = A.one(datePickerContainer).all('select');

				selects.on(
					'change',
					function(event) {
						var currentTarget = event.currentTarget;

						var date = schedulerEvent.get(dateAttr);

						var selectedSetter = selects.indexOf(currentTarget);

						var setters = [date.setMonth, date.setDate, date.setFullYear, date.setHours, date.setMinutes, date.setHours];

						var value = toInt(currentTarget.val());

						if (selectedSetter === 3 && date.getHours() > 12) {
							value += 12;
						}

						if (selectedSetter === 5) {
							value = date.getHours() + (value === 1 ? 12 : -12);
						}

						setters[selectedSetter].call(date, value);

						schedulerEvent.get('scheduler').syncEventsUI();
					}
				);
			}
		};
	},
	'',
	{
		requires: ['aui-base']
	}
);