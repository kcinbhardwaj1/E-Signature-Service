package com.rgp.de.service;

import java.io.IOException;
import java.util.List;

import com.docusign.esign.client.ApiException;
import com.rgp.de.beans.DocumentDTO;
import com.rgp.de.beans.DocumentResult;
import com.rgp.de.beans.EmbedSummary;
import com.rgp.de.beans.Envelope;
import com.rgp.de.beans.EnvelopeDefination;
import com.rgp.de.beans.EnvelopeQueryStrings;
import com.rgp.de.beans.EnvelopesInformation;
import com.rgp.de.beans.RemoteSummary;

public interface EnvelopeService {

	/**
	 * @param envelope
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	public RemoteSummary createAndSendEnvelopeByTemplate(EnvelopeDefination envelope) throws ApiException, IOException;

	/**
	 * @param envelope
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	public RemoteSummary createAndSendEnvelope(EnvelopeDefination envelope) throws ApiException, IOException;

	/**
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	public EnvelopesInformation fetchAllEnvelopes(Integer days) throws ApiException, IOException;

	/**
	 * @param envelopeId
	 * @return
	 * @throws ApiException
	 * @throws IOException
	 */
	public List<DocumentDTO> getAllDocumntsByEnvelopeId(String envelopeId) throws ApiException, IOException;

	/**
	 * @param envelopeId
	 * @param docId
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public DocumentDTO getDocumentOfEnvelopeByDocuId(String envelopeId, String docId) throws IOException, ApiException;

	/**
	 * @param envelopeId
	 * @param docId
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public DocumentResult deleteDocumentById(String envelopeId, String docId) throws IOException, ApiException;

	/**
	 * @param envelope
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public EmbedSummary embeddedSigningByTemplate(EnvelopeDefination envelope) throws IOException, ApiException;

	/**
	 * @param envelope
	 * @return
	 * @throws IOException
	 * @throws ApiException
	 */
	public EmbedSummary embeddedSigning(EnvelopeDefination envelope) throws IOException, ApiException;

	/**
	 * @param envelopeQueryStrings
	 * @return
	 * @throws IOException
	 * @throws ApiException Gets status changes for one or more envelopes. Retrieves
	 *                      envelope status changes for all envelopes. You can
	 *                      modify the information returned by adding query strings
	 *                      to limit the request to check between certain dates and
	 *                      times, or for certain envelopes, or for certain status
	 *                      codes. It is recommended that you use one or more of the
	 *                      query strings in order to limit the size of the
	 *                      response. ### Important: Unless you are requesting the
	 *                      status for specific envelopes (using the
	 *                      &#x60;envelopeIds&#x60; or &#x60;transactionIds&#x60;
	 *                      properties), you must add a set the
	 *                      &#x60;from_date&#x60; property in the request. Getting
	 *                      envelope status using &#x60;transactionIds&#x60; is
	 *                      useful for offline signing situations where it can be
	 *                      used determine if an envelope was created or not, for
	 *                      the cases where a network connection was lost, before
	 *                      the envelope status could be returned. ### Request
	 *                      Envelope Status Notes ### The REST API GET /envelopes
	 *                      call uses certain filters to find results. In some cases
	 *                      requests are check for \&quot;any status change\&quot;
	 *                      instead of the just the single status requested. In
	 *                      these cases, more envelopes might be returned by the
	 *                      request than otherwise would be. For example, for a
	 *                      request with the begin date is set to Jan 1st, an end
	 *                      date set to Jan 7th and the Valid Current Statuses
	 *                      column) for the status qualifiers in the request. If the
	 *                      status and status qualifiers in the API request do not
	 *                      contain any of the values shown in the valid current
	 *                      statuses column, then an empty list is returned. For
	 *                      example, a request with a status qualifier
	 *                      (from_to_status) of &#x60;Delivered&#x60; and a status
	 *                      of \&quot;&#x60;Created&#x60;,&#x60;Sent&#x60;\&quot;,
	 *                      DocuSign will always return an empty list. This is
	 *                      because the request essentially translates to: find the
	 *                      envelopes that were delivered between the begin and end
	 *                      dates that have a current status of &#x60;Created&#x60;
	 *                      or &#x60;Sent&#x60;, and since an envelope that has been
	 *                      delivered can never have a status of &#x60;Created&#x60;
	 *                      or &#x60;Sent&#x60;, a zero-size response would be
	 *                      generated. In this case, DocuSign does not run the
	 *                      request, but just returns the empty list. Client
	 *                      applications should check that the statuses they are
	 *                      requesting make sense for a given status qualifier.
	 */
	public EnvelopesInformation filterEnvelopes(EnvelopeQueryStrings envelopeQueryStrings)
			throws IOException, ApiException;

	public void envelopeStatusProcessing(String xmlString);

	public Envelope envelopeStatusPolling(String envelopeId);

}