using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MSCampWebApp
{
    public partial class ReceiptCache : ReceiptBase
    {
        public override void ProcessRequest(HttpContext context)
        {
            if (CacheValid())
            {
                // response cache data
                context.Response.ContentType = "text/json";
                context.Response.Write(cacheObject.ToString());
            }
            else
            {
                base.ProcessRequest(context);
            }
        }

        private Boolean CacheValid()
        {
            if (DateTime.UtcNow < DateTime.MaxValue)
            {
                return true;
            }
            return false;
        }
    }
}