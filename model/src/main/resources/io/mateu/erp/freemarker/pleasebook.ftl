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



<#if transfers??>

    <h2>Transfers</h2>

    <#list transfers as s>
    </#list>


</#if>

<#if hotels??>

    <#list hotels as s>

<div style="font-family: 'Courier New'; font-size: 10px; page-break-after: always;">

    <table width="100%">
        <tr>
            <td width="50%">
                <img src="mylogosrc" width="200px">

                <p style="font-size: 14px;"><b>${s.action!'---'}</b></p>
                <p>REFERENCIA: <b>${s.po}</b><br>
                    NOMBRE PASAJEROS(S): ${s.leadname} x ${s.totalpax} PAX<br>
                    MERCADO ORIGEN: ${s.market}</p>

            </td>
            <td>

                <table>
                    <tr><td>

                        <p><b>VIAJES ES FREUS</b></p>
                        <p>dirección<br>
                            Ciudad CP<br>
                            CIF  ESB3838383838<br>
                            Tel: 971386295<br>
                            Email: hdueduedude@idjiedjei.es</p>
                    </td></tr>
                    <tr><td>

                        <p><b>HOTEL LOS ROSALES</b></p>
                        <p>dirección    CP<br>
                            zona - destino - pais<br>
                            <br>
                            Fecha de notificación 05-DEC-18 19:43<br>
                            Fecha de creación: 05-DEC-18 17:27</p>
                    </td></tr>
                </table>

            </td>
        </tr>
    </table>

    <p></p>

    <b style="font-size: 12px;">RESUMEN:</b>
    <div style="border: 1px solid black;">
        <p>REFERENCIA: ${s.po}</p>

        <#if s.type = 'hotel'>

            <#list s.lines as l>

            <p>Check-in / Check-out: ${l.start} HASTA ${l.end}<br>
            Total noches: ${l.nights}<br>
            Tipo de habitación y régimen: ${l.rooms} x ${l.room} / ${l.board}<br>
            Total pax: ${l.pax} (${l.adults} Adultos / ${l.children} Niños / Edades: ${l.ages})</p>

            </#list>

        </#if>

    </div>

    <p></p>

    <b style="font-size: 12px;">CONTRATO:</b>
    <div style="border: 1px solid black;">
        <p>FECHAS: Desde 14-AUG-19 hasta </p>

        <#if s.type = 'hotel'>

            <#list s.lines as l>

            <p>CONTRATO: ${l.contract}<br>Check-in / Check-out: ${l.start} HASTA ${l.end}<br>
                Total noches: ${l.nights}<br>
                Tipo de habitación y régimen: ${l.rooms} x ${l.room} / ${l.board}<br>
                Total pax: ${l.pax} (${l.adults} Adultos / ${l.children} Niños / Edades: ${l.ages})</p>

            </#list>

        </#if>

    </div>

    <p></p>

    <b style="font-size: 12px;">CUPO:</b>
    <div style="border: 1px solid black;">
        <p>Desde 14-AUG-19 hasta 21-AUG-19 Cupo Incluido</p>
        <#if s.type = 'hotel'>

            <#list s.lines as l>

            <p>CONTRATO: ${l.contract}<br>Check-in / Check-out: ${l.start} HASTA ${l.end}<br>
                Total noches: ${l.nights}<br>
                Tipo de habitación y régimen: ${l.rooms} x ${l.room} / ${l.board}<br>
                Total pax: ${l.pax} (${l.adults} Adultos / ${l.children} Niños / Edades: ${l.ages})</p>

            </#list>

        </#if>
    </div>

    <p></p>

    <b style="font-size: 12px;">INFORMACIÓN GENERAL:</b>
    <div style="border: 1px solid black;">
        <p>Por favor, indique nuestro NÚMERO DE REFERENCIA (${s.po}) en cada una de sus facturas.</p>
    </div>

    <p></p>

    <p style="page-break-before: always;"></p>
    <b style="font-size: 12px;">CONTACTOS:</b>
    <div style="border: 1px solid black;">
        <p><ul style="padding-left: 20px; padding-top: 0px; padding-right: 0px; padding-bottom: 0px;">
        <li>Reservas y Asistencia Hotel: booking@esfreus.com o (+34) 971 38 36 60</li>
        <li>Notificación de no-shows: booking@esfreus.com</li>
        <li>Actualizaciónd e tarifas y disponibilidad en nuestra extranet: http://esfreus.quotravel.eu</li>
        <li>Estado de facturas en: http://esfreus.quotravel.eu</li>
        <li>Ebilling: http://esfreus.quotravel.eu</li>
        <li>Solicite a su channel manager...</li>
        <li>Horario de oficina: L-V 09:00h a 18:00h</li>
    </ul></p>
    </div>

</div>

    </#list>


</#if>





        </td>
        <td bgcolor="aliceblue" style="font-size: 0;">&​nbsp;</td>
    </tr>
</table>
</body>
</html>
