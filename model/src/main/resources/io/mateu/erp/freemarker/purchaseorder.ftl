<div style="font-family: Arial, Helvetica, sans-serif;">

    <img src="mylogosrc" width="200px">

    <h2>${office!}</h2>


    <p>Dear partner,</p>


    <p><b>${postscript!}</b></p>


    <p>please provide these services:</p>


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
                <th>Pickup time</th><td>${s.pickupTime!}</td>
            </tr><tr>
                <th>Dropoff</th><td>${s.dropoff!} / ${s.dropoffResort!}</td>
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
                <th>Lead name</th><td>${s.leadName!} / ${s.agencyReference!}</td>
            </tr><tr>
                <th>Comments</th><td>${s.comments!}</td>
            </tr></table>


        </td>
    </tr>
</table>

<hr/>
    </#list>


</#if>


<#if generics??>

<h2>Other services</h2>
<table style="border-collapse:collapse;" border="1" cellpadding="5">
    <thead style="background-color: lightsteelblue;">
    <th>Ref.</th>
    <th>Status</th>
    <th>Units</th>
    <th>Start</th>
    <th>Finish</th>
    <th>Description</th>
    <th>Lead name</th>
    <th>Comments</th>
    </thead>
<#list generics as s>
    <#list s.lines as l>
  <tr>
      <td>${s.po!}</td>
      <td <#if s.status == 'CANCELLED'> style="background-color: indianred;"</#if><#if s.status == 'ACTIVE'> style="background-color: lightgreen;"</#if>>${s.status!}</td>
      <td>${l.units!}</td>
      <td>${l.start!}</td>
      <td>${l.finish!}</td>
      <td>${l.description!}</td>
      <td>${s.leadName!} / ${s.agencyReference!}</td>
      <td>${s.comments!}</td>
  </tr>
    </#list>
</#list>
</table>

</#if>




    <p>Thanks and best regards,</p>

    <p>${office!}</p>

</div>