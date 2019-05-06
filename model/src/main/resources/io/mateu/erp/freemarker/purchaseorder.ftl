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







            <div style="font-family: Arial, Helvetica, sans-serif;">

    <img src="mylogosrc" width="200px">

    <h2>${office!}</h2>


    <p>Dear partner,</p>


    <p><b>${postscript!}</b></p>


    <p>please provide these services:</p>


                <#if hotels??>

<h2>Hotels</h2>

                    <#list hotels as s>
<table width="100%">
    <tr>
        <td width="50%" style="vertical-align: top;">

            <table style="border-collapse:collapse; width: 100%;" border="1" cellpadding="5">
                <tr>
                    <th>Ref.</th><td>${s.po!}</td>
                </tr><tr>
                <th>Status</th><td <#if s.status == 'CANCELLED'> style="background-color: indianred;"</#if><#if s.status == 'ACTIVE'> style="background-color: lightgreen;"</#if>>${s.status!}</td>
            </tr><tr>
                <th>Hotel</th><td>${s.hotel!}</td>
            </tr></table>

        </td>
        <td width="50%" style="vertical-align: top;">

            <table style="border-collapse:collapse; width: 100%;" border="1" cellpadding="5">
                <tr>
                <th>Agency</th><td>${s.agency!}</td>
            </tr><tr>
                <th>Lead name</th><td>${s.leadName!} / ${s.agencyReference!}</td>
            </tr></table>


        </td>
    </tr>
</table>
<table width="100%" border="1">
    <tr><th>Checkin</th><th>Checkout</th><th>Room</th><th>Board</th><th>Nr of rooms</th><th>Adults / Room</th><th>Children / Room</th><th>Children ages</th></tr>
                        <#if s.lines??>
                            <#list s.lines as l>
    <tr><td>${l.start!}</td><td>${l.end!}</td><td>${l.room!}</td><td>${l.board!}</td><td>${l.rooms!}</td><td>${l.adults!}</td><td>${l.children!}</td><td>${l.ages!}</td></tr>
                            </#list>
                        </#if>

</table>

                        <#if s.comments??>
                    <br/>
                        <b>Comments:</b>
                        <pre>${s.comments}</pre>
                        </#if>


                                                <#if s.confirmationUrl??>
                    <br/>

                        <a href="${s.confirmationUrl}">Please confirm this service</a>
                        </#if>

<hr/>
                    </#list>


                </#if>

<#if transfers??>

<h2>Transfers</h2>

    <#list transfers as s>
<table width="100%">
    <tr>
        <td width="50%" style="vertical-align: top;">

            <table style="border-collapse:collapse; width: 100%;" border="1" cellpadding="5">
                <tr>
                    <th>Ref.</th><td>${s.po!}</td>
                </tr><tr>
                <th>Status</th><td <#if s.status == 'CANCELLED'> style="background-color: indianred;"</#if><#if s.status == 'ACTIVE'> style="background-color: lightgreen;"</#if>>${s.status!}</td>
            </tr><tr>
                <th>Service</th><td>${s.transferType!}</td>
            </tr><tr>
                <th>Vehicle</th><td>${s.preferredVehicle!}</td>
            </tr><tr>
                <th>Direction</th><td>${s.direction!}</td>
            </tr><tr>
                <th>Pickup</th><td>${s.pickup!} / ${s.pickupResort!}</td>
            </tr><tr>
                <th>Effective pickup</th><td>${s.effectivePickup!} / ${s.effectivePickupResort!}</td>
            </tr><tr>
                <th>Pickup time</th><td>${s.pickupTime!}</td>
            </tr><tr>
                <th>Dropoff</th><td>${s.dropoff!} / ${s.dropoffResort!}</td>
            </tr><tr>
                <th>Effective dropoff</th><td>${s.effectiveDropoff!} / ${s.effectiveDropoffResort!}</td>
            </tr></table>

        </td>
        <td width="50%" style="vertical-align: top;">

            <table style="border-collapse:collapse; width: 100%;" border="1" cellpadding="5">
                <tr>
                    <th>Flight</th><td>${s.flight!}</td>
                </tr><tr>
                <th>Flight date</th><td>${s.flightDate!}</td>
            </tr><tr>
                <th>Flight time</th><td>${s.flightTime!}</td>
            </tr><tr>
                <th>Origin/destination</th><td>${s.flightOriginOrDestination!}</td>
            </tr><tr>
                <th>Pax</th><td>${s.pax!}</td>
            </tr><tr>
                <th>Agency</th><td>${s.agency!}</td>
            </tr><tr>
                <th>Lead name</th><td>${s.leadName!} / ${s.agencyReference!}</td>
            </tr><tr>
                <th>Comments</th><td>${s.comments!}</td>
            </tr></table>


        </td>
    </tr>
</table>

        <#if s.confirmationUrl??>
                    <br/>

                        <a href="${s.confirmationUrl}">Please confirm this service</a>
        </#if>

<hr/>
    </#list>


</#if>


<#if generics??>

<h2>Other services</h2>
<#list generics as s>
<table style="border-collapse:collapse;" border="1" cellpadding="5">
    <thead style="background-color: lightsteelblue;">
    <th>Ref.</th>
    <th>Status</th>
    <th>Product</th>
    <th>Variant</th>
    <th>Units</th>
    <th>Adults</th>
    <th>Children</th>
    <th>Start</th>
    <th>Finish</th>
    <th>Lead name</th>
    <th>Comments</th>
    </thead>
  <tr>
      <td>${s.po!}</td>
      <td <#if s.status == 'CANCELLED'> style="background-color: indianred;"</#if><#if s.status == 'ACTIVE'> style="background-color: lightgreen;"</#if>>${s.status!}</td>
      <td>${s.product!}</td>
      <td>${s.variant!}</td>
      <td>${s.units!}</td>
      <td>${s.adults!}</td>
      <td>${s.children!}</td>
      <td>${s.start!}</td>
      <td>${s.finish!}</td>
      <td>${s.leadName!} / ${s.agencyReference!}</td>
      <td>${s.comments!}</td>
  </tr>
</table>

    <#if s.confirmationUrl??>
                    <br/>

                        <a href="${s.confirmationUrl}">Please confirm this service</a>
    </#if>

    </#list>



</#if>


                <#if freetexts??>

<h2>Other services</h2>
<#list freetexts as s>
<table style="border-collapse:collapse;" border="1" cellpadding="5">
    <thead style="background-color: lightsteelblue;">
    <th>Ref.</th>
    <th>Status</th>
    <th>Service</th>
    <th>Start</th>
    <th>Finish</th>
    <th>Lead name</th>
    <th>Comments</th>
    </thead>
  <tr>
      <td>${s.po!}</td>
      <td <#if s.status == 'CANCELLED'> style="background-color: indianred;"</#if><#if s.status == 'ACTIVE'> style="background-color: lightgreen;"</#if>>${s.status!}</td>
      <td>${s.text!}</td>
      <td>${s.start!}</td>
      <td>${s.finish!}</td>
      <td>${s.leadName!} / ${s.agencyReference!}</td>
      <td>${s.comments!}</td>
  </tr>
</table>

                    <#if s.confirmationUrl??>
                    <br/>

                        <a href="${s.confirmationUrl}">Please confirm this service</a>
                    </#if>

</#list>
                </#if>









    <p>Thanks and best regards,</p>

    <p>${office!}</p>

</div>






        </td>
        <td bgcolor="aliceblue" style="font-size: 0;">&​nbsp;</td>
    </tr>
</table>
</body>
</html>