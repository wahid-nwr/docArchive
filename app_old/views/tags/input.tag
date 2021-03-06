%{
	String[] pieces = _arg.split("\\.");
	Object obj = _caller.get(pieces[0]);
	this.setProperty(pieces[0], obj);

	if(_type == null) {
		_type = "text";
	}
}%

#{field _arg}
<div class="control-group#{ifError field.name} error#{/ifError}">
	<label class="" for="${field.id}">&{field.name}</label>
	<div class="controls">
	#{if _type == 'select'}
		#{if _multiple}
			#{select required:_required,disabled:_disabled, items:_items, style:'height:100px;',labelProperty:_labelProperty, name:field.name, id:field.name, value:field.value, multiple:'', class:'form-control'}
				#{doBody /}
			#{/select}
		#{/if}
		#{else}
			#{if _required=='true'}
			#{select required:_required, items:_items, labelProperty:_labelProperty, name:field.name, id:field.name, value:field.value, class:'form-control'}
				#{doBody /}
			#{/select}
			#{/if}
			#{elseif _disabled=='true'}
			#{select disabled:_disabled, items:_items, labelProperty:_labelProperty, name:field.name, id:field.name, value:field.value, class:_class}
				#{doBody /}
			#{/select}
			#{/elseif}
			#{else}
			#{select items:_items, labelProperty:_labelProperty, name:field.name, id:field.name, value:field.value, class:'form-control'}
				#{doBody /}
			#{/select}
			#{/else}
		#{/else}
	#{/if}
	#{else}
		*{
		<input #{if _accept}accept="${_accept}" #{/if}id="${field.id}" name="${field.name}" type="${_type}" value="${field.value?.raw()?.escape()}" #{if _required}required#{/if}>
		}*
		#{html5.input for:field.name, id:field.id, class:_class, type:_type, placeholder:_placeholder,disabled:_disabled, required:_required/}
	#{/else}
	<span class="help-inline">#{ifError field.name}${field.error.raw()}#{/ifError}#{else}${_hint}#{/else}</span>
	</div>
</div>
#{/field}
