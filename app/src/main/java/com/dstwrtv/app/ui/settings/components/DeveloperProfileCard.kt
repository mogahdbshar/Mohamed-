package com.dstwrtv.app.ui.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dstwrtv.app.ui.components.DSTWRTheme
import com.dstwrtv.app.ui.components.DstwrLogo

@Composable
fun DeveloperProfileCard(
    totalChannelsCount: Int,
    favoritesCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.2.dp, DSTWRTheme.BorderSoft)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(DSTWRTheme.SecondaryDark, DSTWRTheme.SurfaceDark)
                    )
                )
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(DSTWRTheme.PureBlack, CircleShape)
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                brush = Brush.linearGradient(listOf(DSTWRTheme.PrimaryRed, DSTWRTheme.AccentAmber))
                            ),
                            shape = CircleShape
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DstwrLogo(size = 58.dp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "محمد الدستور",
                        color = DSTWRTheme.TextMain,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.3.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF2196F3), Color(0xFF1976D2))
                                ),
                                shape = CircleShape
                            )
                            .border(0.8.dp, Color(0x73FFFFFF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Verified Badge",
                            tint = Color.White,
                            modifier = Modifier.size(11.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "المطور والمهندس الرئيس لشبكات IPTV الرقمية وتصميم منصة DSTWR TV.\n" +
                           "المشرف العام على استقرار وجدولة خوادم البث المباشر المدمجة، فك تشفير وتثبيت جودة العرض والأداء المستمر.",
                    color = DSTWRTheme.TextMuted,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DSTWRTheme.SecondaryDark, RoundedCornerShape(12.dp))
                            .border(1.dp, DSTWRTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalChannelsCount",
                                color = DSTWRTheme.PrimaryRed,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "قناة متوفرة",
                                color = DSTWRTheme.TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DSTWRTheme.SecondaryDark, RoundedCornerShape(12.dp))
                            .border(1.dp, DSTWRTheme.BorderSoft.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$favoritesCount",
                                color = DSTWRTheme.AccentAmber,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "قناة مفضلة",
                                color = DSTWRTheme.TextMuted,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
