using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MSCampWebApp
{
    public partial class ReceiptChecksumWithTimeLimited : ReceiptBase
    {
        public override void ProcessRequest(HttpContext context)
        {
            String now = context.Request.Params["now"];
            String checksum = context.Request.Params["checksum"];
            if (!ChecksumValid(now, checksum))
            {
                context.Response.StatusCode = 404;
            }
            else
            {
                base.ProcessRequest(context);
            }
        }

        private Boolean ChecksumValid(String now, String checksum)
        {
            long clientNow;
            if (long.TryParse(now, out clientNow))
            {
                long epochNow = Decimal.ToInt64(Decimal.Divide(DateTime.UtcNow.Ticks - new DateTime(1970, 1, 1, 0, 0, 0).Ticks, 10000000));
                if (Math.Abs(epochNow - clientNow) < 60)
                {
                    // 容許 1 分鐘的誤差
                    String validChecksum = getMD5Hash(now + "Ascii_Receipt_Open_Camp");
                    return validChecksum.ToLower().Equals(checksum);
                }
            }
            return false;
        }
    }
}