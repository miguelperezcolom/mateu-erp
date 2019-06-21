<!DOCTYPE html>
<html lang="en">
<head>
    <title>Booking email</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <style type="text/css">
        /* CLIENT-SPECIFIC STYLES */
        body, table, td, a { -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }
        table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
        img { -ms-interpolation-mode: bicubic; }

        /* RESET STYLES */
        img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; }
        table { border-collapse: collapse !important; }
        body { height: 100% !important; margin: 0 !important; padding: 0 !important; width: 100% !important; }
    </style>
</head>
<body style="background-color: aliceblue; margin: 0 !important; padding: 60px 0 60px 0 !important;"><table border="0" cellspacing="0" cellpadding="0" role="presentation" width="100%">
    <tr>
        <td bgcolor="aliceblue" style="font-size: 0;"> </td>
        <td bgcolor="white" width="600" style="border-radius: 4px; font-family: sans-serif; padding: 20px 40px;">

            <table width="100%"><tr><td><p>Estimado/a ${leadName},</p>

<p>Muchas gracias por reservar con ${us!}.</p>

<p>${postscript!}</p>

<#if availstatus == 'CANCELADA' >
<p>Su reserva ha sido cancelada. Para cualquier aclaración diríjase por favor a ${bookingemail!}.</p>
<#else>
<p>Por favor, verifique los datos de su reserva que se muestran más abajo. En el caso de que fueran incorrectos comuníquenoslo en la mayor brevedad posible a ${bookingemail!}.</p>
</#if>


            </td></tr>

                <tr><td style="padding-bottom: 0px;border-bottom: 1px dashed dimgrey;">
                    <hr><table width="100%"><tr><td><span style="font-size: 20px; font-weight: bold;">LOCALIZADOR RESERVA: ${locator}</span></td><td align="right"><span><b>Fecha reserva: ${formalization}</b></span></td></tr></table>
                    </td></tr>
                <tr><td style="border-bottom: 1px solid dimgrey; background-color: <#switch availstatus>
                    <#case "CANCELADA">indianred<#break>
                    <#case "CONFIRMADA">darkseagreen<#break>
                    <#case "ON REQUEST">lightblue<#break>
                    <#default>lightgoldenrodyellow</#switch>;"><span style="font-size: 20px;">ESTADO DE LA RESERVA: ${availstatus} / ${paymentstatus}</span></td></tr>

<#if availstatus != 'CANCELADA' >
                <tr><td>

<!--
<p>Puede realizar cambios, pagos, imprimir su reserva y consultar su estado desde la siguiente dirección: <a
                        href="">Gestionar mi reserva</a></p>
                        -->
                    <p></p>
</td></tr>


                <#if paymentlink?? >

                <tr><td style="border-top: 1px dashed dimgrey;border-bottom: 1px dashed dimgrey;">

                    <center>
                        <p></p>
                        ${paymentlink!}
                        <p></p>
                        <#if expirydate?? >
                        <p>Dispone hasta <b>${expirydate!}</b> para realizar el pago de la reserva: ${totalretail}</p>
                        </#if>
                    </center>

                    <p></p>
                </td></tr>

                </#if>
</#if>

<!--
                <tr><td style="border-top: 1px dashed dimgrey;border-bottom: 1px dashed dimgrey;">
<p>Dispone de <b>24 horas hábiles</b> para realizar el pago de la reserva mediante ingreso bancario, transfiriendo la cantidad de <b>${paymentamounttxt!'---'}</b> al número de cuenta indicado a continuación:</p>

<table width="100%">
    <tr><td width="33%"><b>Nombre del banco:</b></td><td>${bankname!'---'}</td></tr>
    <tr><td><b>Dirección del banco:</b></td><td>${bankaddress!'---'}</td></tr>
    <tr><td><b>Beneficiario:</b></td><td>${recipient!'---'}</td></tr>
    <tr><td><b>Número de cuenta:</b></td><td>${accountnumber!'---'}</td></tr>
    <tr><td><b>SWIFT:</b></td><td>${swift!'---'}</td></tr>
    <tr><td><b>Cantidad:</b></td><td>${paymentamounttxt!'---'}</td></tr>
    <tr><td><b>Concepto:</b></td><td>RESERVA ${locator}</td></tr>
</table>

<p>Una vez efectuada la transferencia deberá mandarnos el <b>justificante</b> por <b>e-mail</b> para enviarle el bono de confirmacion lo antes posible.</p>

<table width="100%">
    <tr><td width="33%"><b>E-mail:</b></td><td>${bookingemail!}</td></tr>
</table>
                    <p></p>

<b>IMPORTANTE: Debe indicar en el concepto de la transferencia el localizador de su reserva o será imposible procesar su reserva.</b>

<p></p>
</td></tr>

-->

                <tr><td><p></p><p></p><p></p><p></p><h2 style="margin-bottom: 0px;">DATOS RESERVA</h2>
                    <hr style="margin-top: 0px;"></td></tr>
<tr><td>

    <#if availstatus == 'CANCELADA' > <div style="text-decoration: line-through; white-space: pre;"> </#if>${servicedata}<#if availstatus == 'CANCELADA' > </div> </#if>

    <p></p><p></p><p></p>
                <table width="100%">
                    <tr><td width="50%"><h2 style="margin-bottom: 0px;">DATOS VIAJERO</h2>

                        <hr style="margin-top: 0px;"></td><td><h2 style="margin-bottom: 0px;">DATOS CLIENTE</h2>

                        <hr style="margin-top: 0px;"></td></tr>
                    <tr><td style="vertical-align: top;">


                        <table>
                            <tr><td width='140px'>Nombre</td><td>${leadName}</td></tr>
                            <tr><td>Teléfono</td><td>${telephone!'---'}</td></tr>
                            <tr><td>E-mail</td><td>${email!'---'}</td></tr>
                        </table>

                    </td><td>


                        <table>
                            <tr><td width='140px'>Agencia</td><td>${agency!'---'}</td></tr>
                            <tr><td>Dirección</td><td>${agencyaddress!'---'}</td></tr>
                            <tr><td>NIF</td><td>${agencyvatid!'---'}</td></tr>
                            <tr><td>Nombre del agente</td><td>${agentname!'---'}</td></tr>
                            <tr><td>E-mail del agente</td><td>${agencyemail!'---'}</td></tr>
                            <tr><td>Teléfono del agente</td><td>${agencytelephone!'---'}</td></tr>
                        </table>

                    </td></tr>
                </table>


<p></p><p></p><p></p>

        ${productdata}



<#if availstatus != 'CANCELADA' >
                <h2 style="margin-bottom: 0px;">DATOS DE PAGO</h2>

                <hr style="margin-top: 0px;">

                <table width="100%">
                    <tr><td width="50%" style="vertical-align: top;">
                        <p></p>

                        <table width="100%">
                            <tr><td width="200px">Precio total (IVA incl.)</td><td align="right">${totalretail}</td></tr>
                            <!--
                            <tr><td>Precio total profesional (IVA incl.)</td><td align="right">${totalnet}</td></tr>
                            -->
                            <tr><td colspan="2">Método de pago: ${paymentmethod!'---'}</td></tr>

                        ${payments!'---'}

                        </table>

                        <center>
                        <p>${paymentlink!}</p>
                        </center>


                    </td><td style="border-left: 1px dashed black; padding-left: 10px; vertical-align: top;">

                        ${paymentremarks!'---'}

                    </td></tr>
                </table>

    <p></p><p></p><p></p>

                <h2 style="margin-bottom: 0px;">TÉRMINOS Y CONDICIONES</h2>
                <hr style="margin-top: 0px;">

                <p><b>Condiciones de reserva:</b></p>

        ${bookingterms!'---'}


    <p></p><p></p><p></p>

                <h2 style="margin-bottom: 0px;">POLÍTICA DE CANCELACIÓN</h2>
                <hr style="margin-top: 0px;">
                <p><b>Condiciones de cancelación:</b></p>


        ${cancellationterms!'---'}


</#if>

            </td></tr></table>


        </td>
        <td bgcolor="aliceblue" style="font-size: 0;">&​nbsp;</td>
    </tr>
</table>
</body>
</html>


