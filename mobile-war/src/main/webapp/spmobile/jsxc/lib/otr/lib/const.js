/*
 * Copyright (C) 2000 - 2021 Silverpeas
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * As a special exception to the terms and conditions of version 3.0 of
 * the GPL, you may redistribute this Program in connection with Free/Libre
 * Open Source Software ("FLOSS") applications as described in Silverpeas's
 * FLOSS exception.  You should have received a copy of the text describing
 * the FLOSS exception, and it is also available here:
 * "http://www.silverpeas.org/docs/core/legal/floss_exception.html"
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

;(function () {
  "use strict";

  var root = this

  var CONST = {

    // diffie-heilman
      N : 'FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF'
    , G : '2'

    // otr message states
    , MSGSTATE_PLAINTEXT : 0
    , MSGSTATE_ENCRYPTED : 1
    , MSGSTATE_FINISHED  : 2

    // otr auth states
    , AUTHSTATE_NONE               : 0
    , AUTHSTATE_AWAITING_DHKEY     : 1
    , AUTHSTATE_AWAITING_REVEALSIG : 2
    , AUTHSTATE_AWAITING_SIG       : 3

    // whitespace tags
    , WHITESPACE_TAG    : '\x20\x09\x20\x20\x09\x09\x09\x09\x20\x09\x20\x09\x20\x09\x20\x20'
    , WHITESPACE_TAG_V2 : '\x20\x20\x09\x09\x20\x20\x09\x20'
    , WHITESPACE_TAG_V3 : '\x20\x20\x09\x09\x20\x20\x09\x09'

    // otr tags
    , OTR_TAG       : '?OTR'
    , OTR_VERSION_1 : '\x00\x01'
    , OTR_VERSION_2 : '\x00\x02'
    , OTR_VERSION_3 : '\x00\x03'

    // smp machine states
    , SMPSTATE_EXPECT0 : 0
    , SMPSTATE_EXPECT1 : 1
    , SMPSTATE_EXPECT2 : 2
    , SMPSTATE_EXPECT3 : 3
    , SMPSTATE_EXPECT4 : 4

    // unstandard status codes
    , STATUS_SEND_QUERY  : 0
    , STATUS_AKE_INIT    : 1
    , STATUS_AKE_SUCCESS : 2
    , STATUS_END_OTR     : 3

  }

  if (typeof module !== 'undefined' && module.exports) {
    module.exports = CONST
  } else {
    root.OTR.CONST = CONST
  }

}).call(this)