using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;
using System.Net;
using System.Security.Cryptography;
using System.Text;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;

namespace MSCampWebApp
{
    public partial class ReceiptBase : System.Web.UI.Page
    {
        protected JObject cacheObject;

        public ReceiptBase()
        {
            cacheObject = JObject.Parse("{\"receipts\":[{\"item\":\"發票中獎號碼資訊\",\"value\":\"cache\"},{\"item\":\"特別獎\",\"value\":\"cache\"},{\"item\":\"特獎\",\"value\":\"cache\"},{\"item\":\"頭獎\",\"value\":\"cache\"},{\"item\":\"增開六獎\",\"value\":\"cache\"}]}");
        }

        public override void ProcessRequest(HttpContext context)
        {
            String html = GetHtml();
            List<Award> list = parseReceiptHtml(html);
            JObject rootObject = new JObject();
            JArray receiptItems = new JArray();
            for (int i = 0; i < list.Count; ++i)
            {
                JObject jObj = new JObject();
                jObj.Add("item", list[i].Title);
                jObj.Add("value", list[i].Description);
                receiptItems.Add(jObj);
            }
            rootObject.Add("receipts", receiptItems);
            context.Response.ContentType = "text/json";
            context.Response.Write(rootObject.ToString());
        }

        protected String getMD5Hash(String source)
        {
            String strResult = "";

            using (MD5 md5Hash = MD5.Create())
            {
                byte[] data = md5Hash.ComputeHash(Encoding.UTF8.GetBytes(source));
                StringBuilder sBuilder = new StringBuilder();
                for (int i = 0; i < data.Length; i++)
                {
                    sBuilder.Append(data[i].ToString("x2"));
                }
                strResult = sBuilder.ToString();
            }

            return strResult;
        }

        private string GetHtml()
        {
            string data = string.Empty;
            string url = "http://invoice.etax.nat.gov.tw/";
            HttpWebRequest tRequest = WebRequest.Create(url) as HttpWebRequest;
            tRequest.Method = WebRequestMethods.Http.Post;
            tRequest.ContentType = "application/x-www-form-urlencoded";

            using (HttpWebResponse tResponse = tRequest.GetResponse() as HttpWebResponse)
            {
                using (StreamReader tReader = new StreamReader(tResponse.GetResponseStream(), Encoding.GetEncoding("UTF-8")))
                {
                    data = tReader.ReadToEnd();
                }
            }
            return data;
        }
        
        private List<Award> parseReceiptHtml(String html)
        {
            List<Award> listRes = new List<Award>();

            try
            {
                int mon = html.IndexOf("月統一發票中獎號碼單", 7500);
                int isno = 0;
                int Seek = 0;
                String strMon = html.Substring(mon - 9, 18).Trim();
                if (strMon != null)
                {
                    strMon = strMon.Trim(new char[] { '<', '>' });
                    strMon = strMon.Replace("統一發票", "");
                    if (!"".Equals(strMon))
                    {
                        listRes.Add(new Award() { Title = "發票中獎號碼資訊", Description = strMon });
                    }
                }

                double dubleVal = 0;
                int nSpecialIdx = html.IndexOf("特別", 0);
                if (nSpecialIdx > 0)
                {
                    // 特別獎不一定每次都有…需特別判斷處理
                    for (Seek = nSpecialIdx; Seek < html.Length; Seek++)
                    {
                        if (double.TryParse(html.Substring(Seek, 1), NumberStyles.Integer, null, out dubleVal))
                        {
                            isno++;
                            if (isno == 8)
                            {
                                break;
                            }
                        }
                        else
                        {
                            isno = 0;
                        }
                    }
                    String strSpecialNumber = html.Substring(Seek - 7, 8);
                    if (strSpecialNumber != null && !"".Equals(strSpecialNumber))
                    {
                        listRes.Add(new Award() { Title = "特別獎", Description = strSpecialNumber });
                    }
                }
                else
                {
                    listRes.Add(new Award() { Title = "特別獎", Description = "本月無加開特別獎" });
                }

                int nSuperIdx = html.IndexOf("特獎", 0);
                for (Seek = nSuperIdx; Seek < html.Length; Seek++)
                {
                    if (double.TryParse(html.Substring(Seek, 1), NumberStyles.Integer, null, out dubleVal))
                    {
                        isno++;
                        if (isno == 8)
                        {
                            break;
                        }
                    }
                    else
                    {
                        isno = 0;
                    }
                }
                String strSuperNumber = html.Substring(Seek - 7, 8);
                if (strSuperNumber != null && !"".Equals(strSuperNumber))
                {
                    listRes.Add(new Award() { Title = "特獎", Description = strSuperNumber });
                }

                int nBigIdx = html.IndexOf("頭獎", 0);
                string[] aStrBigNumber = new string[3];
                for (int i = 0; i < 3; i++)
                {
                    for (Seek = nBigIdx; Seek < html.Length; Seek++)
                    {
                        if (double.TryParse(html.Substring(Seek, 1), NumberStyles.Integer, null, out dubleVal))
                        {
                            isno++;
                            if (isno == 8)
                            {
                                break;
                            }
                        }
                        else
                        {
                            isno = 0;
                        }
                    }
                    aStrBigNumber[i] = html.Substring(Seek - 7, 8);
                    nBigIdx = Seek;
                }
                if (aStrBigNumber[0] != null && !"".Equals(aStrBigNumber[0]))
                {
                    String description = String.Format("{0}\n{1}\n{2}", aStrBigNumber[0], aStrBigNumber[1], aStrBigNumber[2]);
                    listRes.Add(new Award() { Title = "頭獎", Description = description });
                }

                // http://www.chinatimes.com/newspapers/20160229000435-260102
                int nExIdx = html.IndexOf("增開", 0);
                string strExNumber = "";
                if (nExIdx > 0)
                {
                    for (Seek = nExIdx; Seek < html.Length; Seek++)
                    {
                        if (double.TryParse(html.Substring(Seek, 1), NumberStyles.Integer, null, out dubleVal))
                        {
                            isno++;
                            if (isno == 3)
                            {
                                break;
                            }
                        }
                        else
                        {
                            isno = 0;
                        }
                    }

                    if (isno == 3)
                    {
                        strExNumber = html.Substring(Seek - 2, 3);
                        nExIdx = Seek;
                    }
                }
                if (strExNumber != null && !"".Equals(strExNumber))
                {
                    listRes.Add(new Award() { Title = "增開六獎", Description = strExNumber });
                }
            }
            catch (Exception)
            {
            }

            return listRes;
        }
    }
}