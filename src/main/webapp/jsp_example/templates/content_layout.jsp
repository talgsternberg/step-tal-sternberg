<table>
    <tr>
        <td style="width: 200px; border 1px solid black">
            <jsp:include page="side_nav.jsp" />            
        </td>
        <td style="width: 500px; border 1px solid black">
            <jsp:text>
                ${param.content}
            </jsp:text>
        </td>
    </tr>
</table>