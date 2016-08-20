using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MSCampWebApp
{
    public partial class ReceiptChecksum : ReceiptBase
    {
        public override void ProcessRequest(HttpContext context)
        {
            String now = context.Request.Params["now"];
            String checksum = context.Request.Params["checksum"];
            if (!checksumValid(now, checksum))
            {
                context.Response.StatusCode = 404;
            }
            else
            {
                base.ProcessRequest(context);
            }
        }

        private Boolean checksumValid(String now, String checksum)
        {
            String validChecksum = getMD5Hash(now + "Ascii_Receipt_Open_Camp");
            return validChecksum.ToLower().Equals(checksum);
        }
    }
}