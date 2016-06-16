<%@ include file="/WEB-INF/view/module/radiology/template/include.jsp"%>
<%@ include file="/WEB-INF/view/module/radiology/template/includeDatatablesWithDefaults.jsp"%>
<openmrs:htmlInclude file="/moduleResources/radiology/scripts/moment/moment-with-locales.min.js" />

<script type="text/javascript">
  // configure current locale as momentjs default, fall back to "en" if locale not found
  moment.locale([jsLocale, 'en']);

  var $j = jQuery.noConflict();
  $j(document)
          .ready(
                  function() {
                    var patientUuid = $j('#patientUuid');
                    var find = $j('#findButton');
                    var clearResults = $j('a#clearResults');

                    var radiologyOrdersTable = $j('#radiologyOrdersTable')
                            .DataTable(
                                    {
                                      "processing": true,
                                      "serverSide": true,
                                      "ajax": {
                                        headers: {
                                          Accept: "application/json; charset=utf-8",
                                          "Content-Type": "text/plain; charset=utf-8",
                                        },
                                        cache: true,
                                        dataType: "json",
                                        url: "${pageContext.request.contextPath}/ws/rest/v1/radiologyorder/",
                                        data: function(data) {
                                          return {
                                            startIndex: data.start,
                                            limit: data.length,
                                            v: "full",
                                            patient: patientUuid.val(),
                                            totalCount: true,
                                          };
                                        },
                                        "dataFilter": function(data) {
                                          var json = $j.parseJSON(data);
                                          json.recordsTotal = json.totalCount || 0;
                                          json.recordsFiltered = json.totalCount || 0;
                                          json.data = json.results;
                                          return JSON.stringify(json);
                                        }
                                      },
                                      "columns": [
                                          {
                                            "name": "orderNumber",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return '<a href="${pageContext.request.contextPath}/module/radiology/radiologyOrder.form?orderId='
                                                      + full.uuid
                                                      + '">'
                                                      + full.orderNumber
                                                      + '</a>';
                                            }
                                          },
                                          {
                                            "name": "patient",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.patient.display;
                                            }
                                          },
                                          {
                                            "name": "urgency",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.urgency;
                                            }
                                          },
                                          {
                                            "name": "concept",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.concept.display;
                                            }
                                          },
                                          {
                                            "name": "orderer",
                                            "render": function(data, type,
                                                    full, meta) {
                                              return full.orderer.display;
                                            }
                                          },
                                          {
                                            "name": "scheduledDate",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.scheduledDate) {

                                                result = moment(
                                                        full.scheduledDate)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          },
                                          {
                                            "name": "dateActivated",
                                            "render": function(data, type,
                                                    full, meta) {
                                              var result = "";
                                              if (full.dateActivated) {

                                                result = moment(
                                                        full.dateActivated)
                                                        .format("LLL");
                                              }
                                              return result;
                                            }
                                          }, ],
                                    });

                    // prevent form submit when user hits enter
                    $j(window).keydown(function(event) {
                      if (event.keyCode == 13) {
                        event.preventDefault();
                        return false;
                      }
                    });

                    find.on('mouseup keyup', function(event) {
                      if (event.type == 'keyup' && event.keyCode != 13) return;
                      radiologyOrdersTable.ajax.reload();
                    });

                    clearResults.on('mouseup keyup', function() {
                      $j('table#searchForm input:text').val('');
                      patientUuid.val('');
                      radiologyOrdersTable.ajax.reload();
                    });
                  });
</script>

<openmrs:hasPrivilege privilege="Add Radiology Orders">
  <br>
  <a href="radiologyOrder.form"><spring:message code="radiology.addOrder" /></a>
  <br>
</openmrs:hasPrivilege>
<br>
<span class="boxHeader"> <b><spring:message code="radiology.radiologyOrders" /></b> <a id="clearResults" href="#"
  style="float: right"> <spring:message code="radiology.clearResults" />
</a>
</span>
<div class="box">
  <table id="searchForm" cellspacing="10">
    <tr>
      <form id="radiologyOrderListForm">
        <td><label><spring:message code="radiology.patient" /></label> <radiology:patientField formFieldName="patient"
            formFieldId="patientUuid" /></td>
        <td><input id="findButton" type="button" value="<spring:message code="radiology.find"/>" /></td>
      </form>
    </tr>
  </table>
  <br>
  <div id="results">
    <table id="radiologyOrdersTable" cellspacing="0" width="100%" class="display nowrap">
      <thead>
        <tr>
          <th><spring:message code="radiology.datatables.column.orderNumber" /></th>
          <th><spring:message code="radiology.datatables.column.patient" /></th>
          <th><spring:message code="radiology.datatables.column.priority" /></th>
          <th><spring:message code="radiology.datatables.column.imagingProcedure" /></th>
          <th><spring:message code="radiology.datatables.column.referringPhysician" /></th>
          <th><spring:message code="radiology.datatables.column.scheduledDate" /></th>
          <th><spring:message code="radiology.datatables.column.dateActivated" /></th>
        </tr>
      </thead>
    </table>
  </div>
</div>
<br />
