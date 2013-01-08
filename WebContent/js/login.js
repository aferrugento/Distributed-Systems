function validForm(form)
{
	if (form.username.value == "")
	{
		alert("Por favor preencha o campo \'username\'.");
		return false;
	}

	if (form.password.value == "")
	{
		alert("Por favor preencha o campo \'password\'.");
		return false;
	} 

	return true;
}
