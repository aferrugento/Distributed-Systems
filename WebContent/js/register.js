function validForm(form)
{
	if (form.username.value == "")
	{
		alert("Por favor preencha o campo \'username\'.)");
		return false;
	}

	if (form.password.value == "")
	{
		alert("Por favor preencha o campo \'password\'.");
		return false;
	} else if (form.password.value != form.password2.value)
	{
		alert("A confirmacao nao coincide com a password escolhida.");
		return false;
	}

	return true;
}
