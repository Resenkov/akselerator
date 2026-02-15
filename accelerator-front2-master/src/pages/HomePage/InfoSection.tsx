import React from 'react'
import { Typography, Stack, Box } from '@mui/material'
import {
	CalendarToday,
	LocationOn,
	Analytics,
	Settings,
} from '@mui/icons-material'

const InfoSection: React.FC = () => {
	return (
		<Stack direction='column' gap={4}>
			<Stack>
				<Typography variant='h5' gutterBottom sx={{ fontWeight: 'bold' }}>
					О системе
				</Typography>
				<Typography variant='body1' paragraph>
					Современная цифровая платформа для учёта рыболовных уловов и контроля
					выполнения установленных квот. Система разработана для упрощения
					отчётности и повышения прозрачности рыболовной деятельности.
				</Typography>
				<Typography variant='body1'>
					Автоматическая проверка квот, удобный ввод данных и подробная
					аналитика помогают эффективно управлять рыболовными ресурсами.
				</Typography>
			</Stack>
			<Box>
				<Typography variant='h5' gutterBottom sx={{ fontWeight: 'bold' }}>
					Преимущества
				</Typography>
				<Stack gap={2} mt={2} direction='column' flexWrap='wrap'>
					<Box sx={{ display: 'flex', alignItems: 'center' }}>
						<CalendarToday sx={{ color: 'primary.main', mr: 2 }} />
						<Typography>Ежедневный учёт уловов</Typography>
					</Box>
					<Box sx={{ display: 'flex', alignItems: 'center' }}>
						<LocationOn sx={{ color: 'primary.main', mr: 2 }} />
						<Typography>Географическая привязка к районам</Typography>
					</Box>
					<Box sx={{ display: 'flex', alignItems: 'center' }}>
						<Analytics sx={{ color: 'primary.main', mr: 2 }} />
						<Typography>Детальная аналитика и отчёты</Typography>
					</Box>
					<Box sx={{ display: 'flex', alignItems: 'center' }}>
						<Settings sx={{ color: 'primary.main', mr: 2 }} />
						<Typography>Гибкое управление квотами</Typography>
					</Box>
				</Stack>
			</Box>
		</Stack>
	)
}

export default InfoSection
